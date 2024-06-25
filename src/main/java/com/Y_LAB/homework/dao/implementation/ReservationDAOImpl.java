package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.roles.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс ДАО слоя для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 1.0
 */
public class ReservationDAOImpl implements ReservationDAO {

    /** Статическое поле которое содержит в себе все брони.*/
    private static final List<Reservation> allReservations = new ArrayList<>();

    public ReservationDAOImpl() {
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservations() {
        return allReservations;
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsByUsers() {
        return allReservations.stream().sorted(Comparator.comparing(o -> o.getUser().getId())).toList();
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsByDate() {
        return allReservations.stream().sorted(Comparator.comparing(Reservation::getStartDate)).collect(Collectors.toList());
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllUserReservations(User user) {
        return allReservations.stream().filter(x -> x.getUser().equals(user)).toList();
    }

    /** {@inheritDoc}*/
    @Override
    public Reservation getReservation(int id) {
        List<Reservation> filterReservations = allReservations.stream().filter(x -> x.getId() == id).toList();
        return filterReservations.isEmpty() ? null : filterReservations.get(0);
    }

    /** {@inheritDoc}*/
    @Override
    public void addReservation(Reservation reservation) {
        if(getReservation(reservation.getId()) == null) {
            allReservations.add(reservation);
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void updateReservation(Reservation reservation) {
        deleteReservation(reservation.getId());
        addReservation(reservation);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteReservation(int id) {
        allReservations.remove(getReservation(id));
    }
}
