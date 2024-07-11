package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.annotation.Auditable;
import com.Y_LAB.homework.dao.ReservationDAO;
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

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsByUsers() {
        return reservationDAO.getAllReservationsByUsers();
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsByDate() {
        return reservationDAO.getAllReservationsByDate();
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsWithReservationPlace(int reservationPlaceId) {
        return reservationDAO.getAllReservationsWithReservationPlace(reservationPlaceId);
    }

    /** {@inheritDoc}*/
    @Auditable
    @Override
    public List<Reservation> getAllUserReservations(long userId) {
        return reservationDAO.getAllUserReservations(userId);
    }

    /** {@inheritDoc}*/
    @Auditable
    @Override
    public Reservation getReservation(long id) {
        return reservationDAO.getReservation(id);
    }

    /** {@inheritDoc}*/
    @Override
    public void saveReservation(Reservation reservation) {
        reservationDAO.saveReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Auditable
    @Override
    public void saveReservation(ReservationRequestDTO reservationRequestDTO, long userId) {
        ReservationPlace reservationPlace =
                reservationPlaceService.getReservationPlace(reservationRequestDTO.getReservationPlaceId());

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .startDate(reservationRequestDTO.getStartDate())
                .endDate(reservationRequestDTO.getEndDate())
                .reservationPlace(reservationPlace).build();

        reservationDAO.saveReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Auditable
    @Override
    public void updateReservation(Reservation reservation) {
        reservationDAO.updateReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Auditable
    @Override
    public void deleteReservation(long id) {
        reservationDAO.deleteReservation(id);
    }
}
