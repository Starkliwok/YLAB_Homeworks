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

    /**
     * Метод для получения коллекции всех броней
     * @return коллекция всех броней
     */
    @Override
    public List<Reservation> getAllReservations() {
        return allReservations;
    }

    /**
     * Метод для получения коллекции всех броней отсортированных по пользователю из {@link ReservationDAOImpl#allReservations}
     * @return коллекция всех броней отсортированных по пользователю
     */
    @Override
    public List<Reservation> getAllReservationsByUsers() {
        return allReservations.stream().sorted(Comparator.comparing(o -> o.getUser().getId())).toList();
    }

    /**
     * Метод для получения коллекции всех броней отсортированных по дате из {@link ReservationDAOImpl#allReservations}
     * @return коллекция всех броней отсортированных по дате
     */
    @Override
    public List<Reservation> getAllReservationsByDate() {
        return allReservations.stream().sorted(Comparator.comparing(Reservation::getStartDate)).collect(Collectors.toList());
    }

    /**
     * Метод для получения коллекции всех броней пользователя из {@link ReservationDAOImpl#allReservations}
     * @param user объект пользователя
     * @return коллекция всех броней пользователя
     */
    @Override
    public List<Reservation> getAllUserReservations(User user) {
        return allReservations.stream().filter(x -> x.getUser().equals(user)).toList();
    }

    /**
     * Метод для получения брони по уникальному идентификатору из {@link ReservationDAOImpl#allReservations}
     * @param id уникальный идентификатор брони
     * @return объект брони
     */
    @Override
    public Reservation getReservation(int id) {
        List<Reservation> filterReservations = allReservations.stream().filter(x -> x.getId() == id).toList();
        return filterReservations.isEmpty() ? null : filterReservations.get(0);
    }

    /**
     * Метод для добавления брони в {@link ReservationDAOImpl#allReservations}
     * @param reservation объект брони
     */
    @Override
    public void addReservation(Reservation reservation) {
        if(getReservation(reservation.getId()) == null) {
            allReservations.add(reservation);
        }
    }

    /**
     * Метод для обновления брони в {@link ReservationDAOImpl#allReservations}
     * @param reservation объект брони
     */
    @Override
    public void updateReservation(Reservation reservation) {
        deleteReservation(reservation.getId());
        addReservation(reservation);
    }

    /**
     * Метод для удаления брони в {@link ReservationDAOImpl#allReservations}
     * @param id уникальный идентификатор брони
     */
    @Override
    public void deleteReservation(int id) {
        allReservations.remove(getReservation(id));
    }
}
