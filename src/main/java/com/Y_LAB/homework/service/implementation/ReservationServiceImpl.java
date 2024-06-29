package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.service.ReservationService;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 2.0
 */
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    /**Поле ДАО слоя для взаимодействия с бронированиями*/
    private final ReservationDAO reservationDAO;

    public ReservationServiceImpl() {
        reservationDAO = new ReservationDAOImpl();
    }

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
    @Override
    public List<Reservation> getAllUserReservations(long userId) {
        return reservationDAO.getAllUserReservations(userId);
    }

    /** {@inheritDoc}*/
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
    @Override
    public void updateReservation(Reservation reservation) {
        reservationDAO.updateReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteReservation(long id) {
        reservationDAO.deleteReservation(id);
    }
}
