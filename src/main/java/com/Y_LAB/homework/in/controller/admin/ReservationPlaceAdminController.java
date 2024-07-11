package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceFullRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.request.SortPlaceTypeRequest;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.constants.ReservationPlaceConstants.RESERVATION_PLACE_CONFERENCE_ROOM_TYPE;
import static com.Y_LAB.homework.constants.ReservationPlaceConstants.RESERVATION_PLACE_WORKPLACE_TYPE;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * Сервлет панели администратора для получения отсортированных мест для бронирований
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH)
public class ReservationPlaceAdminController {

    private final ReservationPlaceMapper reservationPlaceMapper;

    private final ValidatorDTO<ReservationPlaceRequestDTO> validatorPostDTO;

    private final ValidatorDTO<ReservationPlaceFullRequestDTO> validatorPutDTO;

    private final ReservationPlaceService reservationPlaceService;

    @PostMapping(CONTROLLER_SORT_PATH)
    public ResponseEntity<?> getReservationPlacesSortedByType(@RequestBody SortPlaceTypeRequest sortRequest,
                                                              HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            if (sortRequest.getSortType().equals("type")) {
                if (sortRequest.getPlaceType().equalsIgnoreCase(RESERVATION_PLACE_CONFERENCE_ROOM_TYPE)) {
                    List<ReservationPlace> reservationList =
                            reservationPlaceService.getAllReservationPlacesByTypes(new ConferenceRoom());
                    List<ReservationPlaceResponseDTO> reservationResponseDTOS =
                            reservationPlaceMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                } else if (sortRequest.getPlaceType().equalsIgnoreCase(RESERVATION_PLACE_WORKPLACE_TYPE)) {
                    List<ReservationPlace>
                            reservationList = reservationPlaceService.getAllReservationPlacesByTypes(new Workplace());
                    List<ReservationPlaceResponseDTO>
                            reservationResponseDTOS = reservationPlaceMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                } else {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Enter place type"));
                }
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Enter sort type"));
            }
        }
    }

    @PostMapping
    public ResponseEntity<MessageResponse> saveReservationPlace(@RequestBody ReservationPlaceRequestDTO reservationPlaceRequestDTO,
                                                  HttpServletRequest req) throws AuthorizeException, FieldNotValidException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            validatorPostDTO.validate(reservationPlaceRequestDTO);
            reservationPlaceService.saveReservationPlace(reservationPlaceRequestDTO);
            return ResponseEntity.ok(new MessageResponse("The place has been created"));
        }
    }

    @PutMapping
    public ResponseEntity<ReservationPlace> updateReservationPlace(
            @RequestBody ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO,
            HttpServletRequest req) throws FieldNotValidException, AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            validatorPutDTO.validate(reservationPlaceFullRequestDTO);
            ReservationPlace reservationPlace =
                    reservationPlaceService.getReservationPlace(reservationPlaceFullRequestDTO.getId());
            if(reservationPlace == null) {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceFullRequestDTO.getId() + " does not exists");
            }
            if(reservationPlaceFullRequestDTO.getTypeId() == 1) {
                reservationPlace = reservationPlaceMapper.toConferenceRoom(reservationPlaceFullRequestDTO);
            } else {
                reservationPlace = reservationPlaceMapper.toWorkplace(reservationPlaceFullRequestDTO);
            }
            reservationPlaceService.updateReservationPlace(reservationPlace);
            return ResponseEntity.ok(reservationPlace);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReservationPlace(@PathVariable("id") Integer reservationPlaceId,
                                                    HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        if(reservationPlaceService.getReservationPlace(reservationPlaceId) == null) {
            throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
        } else {
            reservationPlaceService.deleteReservationPlace(reservationPlaceId);
            return ResponseEntity.ok(new MessageResponse("Place with id " + reservationPlaceId + " has been deleted"));
        }
    }
}
