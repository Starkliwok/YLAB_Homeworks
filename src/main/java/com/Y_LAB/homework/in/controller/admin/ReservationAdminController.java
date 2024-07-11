package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.request.SortRequest;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * Сервлет панели администратора для управления бронями
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH)
public class ReservationAdminController {

    private final ReservationMapper reservationMapper;

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservation(@PathVariable("id") Long reservationId,
                                            HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            Reservation reservation = reservationService.getReservation(reservationId);
            if (reservation != null) {
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
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            List<Reservation> reservationList = reservationService.getAllReservations();
            List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
            return ResponseEntity.ok(reservationResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReservation(@PathVariable("id") Long reservationId,
                                     HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        Reservation reservation = reservationService.getReservation(reservationId);
        if(reservation != null) {
            reservationService.deleteReservation(reservationId);
            return ResponseEntity.ok(new MessageResponse("The reservation has been deleted"));
        } else {
            throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
        }
    }

    @PostMapping(CONTROLLER_SORT_PATH)
    public ResponseEntity<?> getReservationSortedByField(@RequestBody SortRequest sortRequest,
                                                         HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            switch (sortRequest.getSortType()) {
                case "user" -> {
                    List<Reservation> reservationList = reservationService.getAllReservationsByUsers();
                    List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                }
                case "date" -> {
                    List<Reservation> reservationList = reservationService.getAllReservationsByDate();
                    List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                }
                default -> {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Enter sort type"));
                }
            }
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }
}
