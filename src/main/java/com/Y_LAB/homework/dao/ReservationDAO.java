package com.Y_LAB.homework.dao;

import com.Y_LAB.homework.entity.reservation.Reservation;

import java.util.List;

/**
 * Интерфейс описывает ДАО слой для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 2.0
 */
public interface ReservationDAO {

    /**
     * Метод для получения коллекции всех броней из базы данных
     * @return коллекция всех броней
     */
    List<Reservation> getAllReservations();

    /**
     * Метод для получения коллекции всех броней отсортированных по пользователю из базы данных
     * @return коллекция всех броней отсортированных по уникальному идентификатору пользователя
     */
    List<Reservation> getAllReservationsByUsers();

    /**
     * Метод для получения коллекции всех броней отсортированных по дате из базы данных
     * @return коллекция всех броней отсортированных по дате начала брони
     */
    List<Reservation> getAllReservationsByDate();

    /**
     * Метод для получения коллекции всех броней с определённым местом для бронирования из базы данных
     * @param reservationPlaceId уникальный идентификатор типа места для бронирования
     * @return коллекция всех броней определённого типа места для бронирования
     */
    List<Reservation> getAllReservationsWithReservationPlace(int reservationPlaceId);

    /**
     * Метод для получения коллекции всех броней пользователя из базы данных
     * @param userId уникальный идентификатор пользователя
     * @return коллекция всех броней пользователя
     */
    List<Reservation> getAllUserReservations(long userId);

    /**
     * Метод для получения брони по уникальному идентификатору из базы данных
     * @param id уникальный идентификатор брони
     * @return объект брони
     */
    Reservation getReservation(long id);

    /**
     * Метод для сохранения объекта брони в базу данных
     * @param reservation объект брони
     */
    void saveReservation(Reservation reservation);

    /**
     * Метод для обновления брони в базе данных
     * @param reservation объект брони
     */
    void updateReservation(Reservation reservation);

    /**
     * Метод для удаления брони из базы данных
     * @param id уникальный идентификатор брони
     */
    void deleteReservation(long id);
}
