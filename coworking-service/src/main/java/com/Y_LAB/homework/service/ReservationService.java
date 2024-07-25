package com.Y_LAB.homework.service;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.reservation.Reservation;

import java.util.List;

/**
 * Интерфейс описывает сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 2.0
 */
public interface ReservationService {

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
     * Метод для получения коллекции всех броней с определённым местом для бронирования
     * @param reservationPlaceId уникальный идентификатор типа места для бронирования
     * @return коллекция всех броней определенного типа места для бронирования
     */
    List<Reservation> getAllReservationsWithReservationPlace(int reservationPlaceId);

    /**
     * Метод для получения коллекции всех броней пользователя
     * @param userId уникальный идентификатор пользователя
     * @return коллекция всех броней пользователя
     */
    List<Reservation> getAllUserReservations(long userId) throws ObjectNotFoundException;

    /**
     * Метод для получения брони по уникальному идентификатору
     * @param id уникальный идентификатор брони
     * @return объект брони
     */
    Reservation getReservation(long id) throws ObjectNotFoundException;

    /**
     * Метод для получения брони пользователя по уникальному идентификатору
     * @param id уникальный идентификатор брони
     * @param userId уникальный идентификатор пользователя
     * @return объект брони, который принадлежит указанному пользователю
     */
    Reservation getUserReservation(long id, long userId) throws ObjectNotFoundException;

    /**
     * Метод для сохранения объекта брони
     * @param reservationRequestDTO DTO объект брони
     * @param userId уникальный идентификатор пользователя
     */
    void saveReservation(ReservationRequestDTO reservationRequestDTO, long userId) throws ObjectNotFoundException;

    /**
     * Метод для обновления брони
     * @param reservation объект брони
     */
    void updateReservation(Reservation reservation) throws ObjectNotFoundException;

    /**
     * Метод для удаления брони
     * @param id уникальный идентификатор брони
     */
    void deleteReservation(long id) throws ObjectNotFoundException;

    /**
     * Метод для удаления брони пользователя
     * @param id уникальный идентификатор брони
     * @param userId уникальный идентификатор пользователя
     */
    void deleteUserReservation(long id, long userId) throws ObjectNotFoundException;
}