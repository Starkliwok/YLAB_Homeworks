package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.service.ReservationService;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 1.0
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
    public List<Reservation> getAllUserReservations(User user) {
        return reservationDAO.getAllUserReservations(user);
    }

    /** {@inheritDoc}*/
    @Override
    public Reservation getReservation(int id) {
        return reservationDAO.getReservation(id);
    }

    /** {@inheritDoc}*/
    @Override
    public void addReservation(Reservation reservation) {
        reservationDAO.addReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Override
    public void updateReservation(Reservation reservation) {
        reservationDAO.updateReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteReservation(int id) {
        reservationDAO.deleteReservation(id);
    }
}
