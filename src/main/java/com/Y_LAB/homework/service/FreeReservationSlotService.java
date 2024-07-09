package com.Y_LAB.homework.service;

import com.Y_LAB.homework.model.reservation.ReservationPlace;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс описывает сервис для получения свободных промежутков времён для бронирования помещения
 * @author Денис Попов
 * @version 2.0
 */
public interface FreeReservationSlotService {

    /**
     * Метод для получения всех свободных мест для бронирования
     * @return коллекция всех мест доступных мест для бронирования в качестве ключа и коллекция свободных дат для
     * бронирования этого помещения в качестве значения
     */
    Map<ReservationPlace, List<LocalDate>> getAllAvailablePlaceForReservations();

    /**
     * Метод для получения всех свободных дат для бронирования места
     * @param reservationPlace место для бронирования
     * @return коллекция доступных дат для бронирования указанного помещения
     */
    List<LocalDate> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace);

    /**
     * Метод для получения всех свободных временных промежутков для бронирования места
     * в виде ключа=значение в качестве одиночных слотов длительностью в 1 час, например (08:00=09:00, 09:00-10:00)
     * @param reservationPlace место для бронирования
     * @param localDate день бронирования
     * @return коллекция доступных промежутков времени для бронирования указанного помещения в указанную дату
     */
    Map<LocalTime, LocalTime> getAllAvailableTimesForReservation(ReservationPlace reservationPlace, LocalDate localDate);

    /**
     * Метод для получения всех свободных временных промежутков для бронирования места
     * в виде ключа=значение в качестве одиночных слотов длительностью в 1 час, например (08:00=09:00, 09:00-10:00)
     * @param reservationPlace место для бронирования
     * @param localDate день бронирования
     * @return коллекция доступных промежутков времени для бронирования указанного помещения в указанную дату
     */
    List<String> getAllAvailableTimeListForReservation(ReservationPlace reservationPlace, LocalDate localDate);

    /**
     * Метод для получения всех свободных мест для бронирования на указанную дату
     * @param localDate дата бронирования
     * @return коллекция доступных мест для бронирования по указанной дате
     */
    List<ReservationPlace> getAllAvailablePlacesForReserveDate(LocalDate localDate);
}
