package com.Y_LAB.homework.service;

import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.reservation.ReservationPlace;

import java.util.List;

/**
 * Интерфейс описывает сервис для взаимодействия с местами для бронирований
 * @author Денис Попов
 * @version 2.0
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
     * Метод для получения объекта места для бронирования
     * @param id уникальный идентификатор места
     * @return Объект места для бронирования
     */
    ReservationPlace getReservationPlace(int id);

    /**
     * Метод для сохранения объекта места для бронирования
     * @param reservationPlace объект места для бронирования
     */
    void saveReservationPlace(ReservationPlace reservationPlace);

    /**
     * Метод для сохранения объекта DTO места для бронирования
     * @param reservationPlaceRequestDTO объект места DTO для бронирования
     */
    void saveReservationPlace(ReservationPlaceRequestDTO reservationPlaceRequestDTO);

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
