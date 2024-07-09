package com.Y_LAB.homework.dao;

import com.Y_LAB.homework.model.reservation.ReservationPlace;

import java.util.List;

/**
 * Интерфейс описывает ДАО слой для взаимодействия с местами для бронирования
 * @author Денис Попов
 * @version 2.0
 */
public interface ReservationPlaceDAO {

    /**
     * Метод для получения всех мест для бронирования из базы данных
     * @return Коллекция всех мест для бронирования
     */
    List<ReservationPlace> getAllReservationPlaces();

    /**
     * Метод для получения мест для бронирования по типу места из базы данных
     * @param reservationPlace объект места для бронирования
     * @return Коллекция мест для бронирования определённого типа
     */
    List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace);

    /**
     * Метод для получения объекта места для бронирования из базы данных
     * @param id уникальный идентификатор места
     * @return Объект места для бронирования
     */
    ReservationPlace getReservationPlace(int id);

    /**
     * Метод для сохранения объекта места для бронирования в базы данных
     * @param reservationPlace объект места для бронирования
     */
    void saveReservationPlace(ReservationPlace reservationPlace);

    /**
     * Метод для обновления объекта места для бронирования в базе данных
     * @param reservationPlace объект места для бронирования
     */
    void updateReservationPlace(ReservationPlace reservationPlace);

    /**
     * Метод для удаления объекта места для бронирования из базы данных
     * @param id уникальный идентификатор места для бронирования
     */
    void deleteReservationPlace(int id);
}
