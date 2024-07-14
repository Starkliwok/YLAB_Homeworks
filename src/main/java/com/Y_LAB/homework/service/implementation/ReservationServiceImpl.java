package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.annotation.Auditable;
import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    /**Поле ДАО слоя для взаимодействия с бронированиями*/
    private final ReservationDAO reservationDAO;

    private final ReservationPlaceService reservationPlaceService;

    @Override
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    @Override
    public List<Reservation> getAllReservationsByUsers() {
        return reservationDAO.getAllReservationsByUsers();
    }

    @Override
    public List<Reservation> getAllReservationsByDate() {
        return reservationDAO.getAllReservationsByDate();
    }

    @Override
    public List<Reservation> getAllReservationsWithReservationPlace(int reservationPlaceId) {
        return reservationDAO.getAllReservationsWithReservationPlace(reservationPlaceId);
    }

    @Auditable
    @Override
    public List<Reservation> getAllUserReservations(long userId) throws ObjectNotFoundException {
        List<Reservation> reservations = reservationDAO.getAllUserReservations(userId);
        if(reservations.isEmpty()) {
            throw new ObjectNotFoundException("Reservations for user with id " + userId + " does not exists");
        }
        return reservations;
    }

    @Auditable
    @Override
    public Reservation getReservation(long id) throws ObjectNotFoundException {
        Reservation reservation = reservationDAO.getReservation(id);
        if(reservation == null) {
            throw new ObjectNotFoundException("Reservation with id " + id + " does not exists");
        }
        return reservation;
    }

    @Override
    public void saveReservation(Reservation reservation) {
        reservationDAO.saveReservation(reservation);
    }

    @Auditable
    @Override
    public void saveReservation(ReservationRequestDTO reservationRequestDTO, long userId) throws ObjectNotFoundException {
        ReservationPlace reservationPlace =
                reservationPlaceService.getReservationPlace(reservationRequestDTO.getReservationPlaceId());

        if(reservationPlace == null) {
            throw new ObjectNotFoundException("Place with id " + reservationRequestDTO.getReservationPlaceId() + " does not exists");
        }
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .startDate(reservationRequestDTO.getStartDate())
                .endDate(reservationRequestDTO.getEndDate())
                .reservationPlace(reservationPlace).build();

        reservationDAO.saveReservation(reservation);
    }

    @Auditable
    @Override
    public void updateReservation(Reservation reservation) throws ObjectNotFoundException {
        if(reservationDAO.getReservation(reservation.getId()) == null) {
            throw new ObjectNotFoundException("Reservation with id " + reservation.getId() + " does not exists");
        } else if (reservationPlaceService.getReservationPlace(reservation.getReservationPlace().getId()) == null) {
            throw new ObjectNotFoundException("Place with id " + reservation.getReservationPlace().getId() + " does not exists");
        }
        reservationDAO.updateReservation(reservation);
    }

    @Auditable
    @Override
    public void deleteReservation(long id) throws ObjectNotFoundException {
        if(reservationDAO.getReservation(id) == null) {
            throw new ObjectNotFoundException("Reservation with id " + id + " does not exists");
        }
        reservationDAO.deleteReservation(id);
    }
}
