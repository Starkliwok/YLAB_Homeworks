package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.request.IdWithLocalDateRequest;
import com.Y_LAB.homework.model.request.LocalDateRequest;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.validation.NumberValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.FIELD_ID;

/**
 * Сервлет для взаимодействия с местами для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_RESERVATION_PLACE_PATH)
public class ReservationPlaceController {

    private final ReservationPlaceMapper reservationPlaceMapper;

    private final ReservationPlaceService reservationPlaceService;

    private final FreeReservationSlotService freeReservationSlotService;

    private final NumberValidator numberValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationPlaceResponseDTO> getReservationPlace(@PathVariable("id") Integer reservationPlaceId,
                                                                           HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace == null) {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
            ReservationPlaceResponseDTO reservationPlaceResponseDTO = reservationPlaceMapper.toResponseDTO(reservationPlace);
            return ResponseEntity.ok(reservationPlaceResponseDTO);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @GetMapping
    public ResponseEntity<List<ReservationPlaceResponseDTO>> getAllReservationPlaces(HttpServletRequest req)
            throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();
            List<ReservationPlaceResponseDTO> reservationPlaceResponseDTOS =
                    reservationPlaceMapper.toResponseDTOList(reservationPlaceList);
            return ResponseEntity.ok(reservationPlaceResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @GetMapping(CONTROLLER_DATE_SLOT_PATH + "/{id}")
    public ResponseEntity<ReservationPlaceDateSlotResponseDTO> getAvailableDatesForPlace(@PathVariable("id") Integer reservationPlaceId,
                                                       HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace == null) {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
            List<LocalDate> availableDatesForReservePlace =
                    freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
            ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                    reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
            return ResponseEntity.ok(reservationPlaceDateSlotResponseDTO);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @GetMapping(CONTROLLER_DATE_SLOT_PATH)
    public ResponseEntity<List<ReservationPlaceDateSlotResponseDTO>> getAllAvailableDatesForPlaces(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();
            List<ReservationPlaceDateSlotResponseDTO> freeSlotResponseDTOS = new ArrayList<>();
            for(ReservationPlace reservationPlace : reservationPlaceList) {
                List<LocalDate> availableDatesForReservePlace =
                        freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
                ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                        reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
                freeSlotResponseDTOS.add(reservationPlaceDateSlotResponseDTO);
            }
            return ResponseEntity.ok(freeSlotResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @PostMapping(CONTROLLER_DATE_SLOT_PATH)
    public ResponseEntity<List<ReservationPlace>> getAvailablePlaceForReserveByDate(@RequestBody LocalDateRequest localDateRequest,
                                                                                    HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            List<ReservationPlace> availableReservationPlaces =
                    freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate());
            if(availableReservationPlaces.isEmpty()) {
                throw new ObjectNotFoundException("Place for reservation on date " + localDateRequest.getLocalDate() + " does not exists");
            } else {
                return ResponseEntity.ok(availableReservationPlaces);
            }
        }
    }

    @PostMapping(CONTROLLER_TIME_SLOT_PATH)
    public ResponseEntity<List<String>> getAvailableTimesForReservation
            (@RequestBody IdWithLocalDateRequest idWithLocalDateRequest,
             HttpServletRequest req) throws AuthorizeException, FieldNotValidException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        int reservationPlaceId = idWithLocalDateRequest.getId();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            numberValidator.validate(reservationPlaceId, FIELD_ID);
            ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace != null) {
                return ResponseEntity.ok(freeReservationSlotService
                        .getAllAvailableTimeListForReservation(reservationPlace, idWithLocalDateRequest.getLocalDate()));
            } else {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
        }
    }
}
