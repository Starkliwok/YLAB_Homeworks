package com.Y_LAB.homework.service;

import com.Y_LAB.homework.entity.reservation.ReservationPlace;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс описывает сервис для взаимодействия с местами для бронирований
 * @author Денис Попов
 * @version 1.0
 */
public interface ReservationPlaceService {

    /**
     * Метод для получения всех мест для бронирования
     * @return Коллекция всех мест для бронирования
     */
    List<ReservationPlace> getAllReservationPlaces();

    /**
     * Метод для получения мест для бронирования по типу места
     * @param reservationPlace объект места для бронирования
     * @return Коллекция мест для бронирования определённого типа
     */
    List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace);

    /**
     * Метод для получения мест и доступных дат для бронирования этого места
     * @return Коллекция мест и доступных дат для бронирования места
     */
    Map<ReservationPlace, List<Date>> getAllAvailableReservations();

    /**
     * Метод для получения доступных дат для бронирования места
     * @param reservationPlace объект места для бронирования
     * @return Коллекция доступных дат для бронирования места
     */
    List<Date> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace);

    /**
     * Метод для получения объекта места для бронирования
     * @param id уникальный идентификатор места
     * @return Объект места для бронирования
     */
    ReservationPlace getReservationPlace(int id);

    /**
     * Метод для сохранения объекта места для бронирования
     * @param reservationPlace объект места для бронирования
     */
    void addReservationPlace(ReservationPlace reservationPlace);

    /**
     * Метод для обновления места для бронирования
     * @param reservationPlace объект места для бронирования
     */
    void updateReservationPlace(ReservationPlace reservationPlace);

    /**
     * Метод для удаления места для бронирования
     * @param id уникальный идентификатор места для бронирования
     */
    void deleteReservationPlace(int id);
}
