package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.ReservationPutRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;

/**
 * Сервлет для взаимодействия с бронированиями пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_RESERVATION_PATH)
public class ReservationController {

    private final ReservationMapper reservationMapper;

    private final ValidatorDTO<ReservationRequestDTO> validatorPostDTO;

    private final ValidatorDTO<ReservationPutRequestDTO> validatorPutDTO;

    private final ReservationService reservationService;

    private final ReservationPlaceService reservationPlaceService;

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getUserReservation(@PathVariable("id") Long reservationId,
                                            HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            Reservation reservation = reservationService.getReservation(reservationId);
            if(reservation != null && reservation.getUserId() == userId) {
                ReservationResponseDTO reservationResponseDTO = reservationMapper.toResponseDTO(reservation);
                return ResponseEntity.ok(reservationResponseDTO);
            } else {
                throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
            }
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllUserReservations(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            List<Reservation> reservationList = reservationService.getAllUserReservations(userId);
            List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
            return ResponseEntity.ok(reservationResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @PostMapping
    public ResponseEntity<MessageResponse> saveUserReservation(@RequestBody ReservationRequestDTO reservationRequestDTO,
                                                           HttpServletRequest req) throws AuthorizeException, FieldNotValidException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            validatorPostDTO.validate(reservationRequestDTO);
            reservationService.saveReservation(reservationRequestDTO, userId);
            return ResponseEntity.ok(new MessageResponse("The reservation has been created"));
        }
    }

    @PutMapping
    public ResponseEntity<MessageResponse> updateUserReservation(@RequestBody ReservationPutRequestDTO reservationPutRequestDTO,
                                     HttpServletRequest req) throws AuthorizeException, FieldNotValidException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            long reservationId = reservationPutRequestDTO.getId();
            validatorPutDTO.validate(reservationPutRequestDTO);
            Reservation reservation = reservationService.getReservation(reservationId);
            if(reservation != null && reservation.getUserId() == userId) {
                ReservationPlace reservationPlace =
                        reservationPlaceService.getReservationPlace(reservationPutRequestDTO.getReservationPlaceId());
                reservation = Reservation.builder()
                        .id(reservationId)
                        .userId(userId)
                        .startDate(reservationPutRequestDTO.getStartDate())
                        .endDate(reservationPutRequestDTO.getEndDate())
                        .reservationPlace(reservationPlace)
                        .build();
                reservationService.updateReservation(reservation);
                return ResponseEntity.ok(new MessageResponse("The reservation has been updated"));
            } else {
                throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUserReservation(@PathVariable("id") Long reservationId,
                                                             HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
        Reservation reservation = reservationService.getReservation(reservationId);
        if(reservation != null && reservation.getUserId() == userId) {
            reservationService.deleteReservation(reservationId);
            return ResponseEntity.ok(new MessageResponse("The reservation has been deleted"));
        } else {
            throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
        }
    }
}
