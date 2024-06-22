package com.Y_LAB.homework.dao;

import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.roles.User;

import java.util.List;

/**
 * Интерфейс описывает ДАО слой для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 1.0
 */
public interface ReservationDAO {

    /**
     * Метод для получения коллекции всех броней
     * @return коллекция всех броней
     */
    List<Reservation> getAllReservations();

    /**
     * Метод для получения коллекции всех броней отсортированных по пользователю
     * @return коллекция всех броней отсортированных по пользователю
     */
    List<Reservation> getAllReservationsByUsers();

    /**
     * Метод для получения коллекции всех броней отсортированных по дате
     * @return коллекция всех броней отсортированных по дате
     */
    List<Reservation> getAllReservationsByDate();

    /**
     * Метод для получения коллекции всех броней пользователя
     * @param user объект пользователя
     * @return коллекция всех броней пользователя
     */
    List<Reservation> getAllUserReservations(User user);

    /**
     * Метод для получения брони по уникальному идентификатору
     * @param id уникальный идентификатор брони
     * @return объект брони
     */
    Reservation getReservation(int id);

    /**
     * Метод для сохранения объекта брони
     * @param reservation объект брони
     */
    void addReservation(Reservation reservation);

    /**
     * Метод для обновления брони
     * @param reservation объект брони
     */
    void updateReservation(Reservation reservation);

    /**
     * Метод для удаления брони
     * @param id уникальный идентификатор брони
     */
    void deleteReservation(int id);
}
