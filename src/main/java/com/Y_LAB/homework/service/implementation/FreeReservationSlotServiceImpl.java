package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.util.reservation.ReservationDateTimeGenerator;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс для получения свободных промежутков времён для бронирования помещения
 * @author Денис Попов
 * @version 1.0
 */
@AllArgsConstructor
public class FreeReservationSlotServiceImpl implements FreeReservationSlotService {

    /** Поле сервиса мест для бронирования, предназначенное взаимодействия с местами для бронирования*/
    private ReservationPlaceDAO reservationPlaceDAO;

    /** Поле сервиса бронирований, предназначенное взаимодействия с бронированиями*/
    private ReservationDAO reservationDAO;

    public FreeReservationSlotServiceImpl() {
        reservationPlaceDAO = new ReservationPlaceDAOImpl();
        reservationDAO = new ReservationDAOImpl();
    }

    /**
     * Метод для получения всех свободных мест для бронирования
     * @return коллекция всех мест доступных мест для бронирования в качестве ключа и коллекция свободных дат для
     * бронирования этого помещения в качестве значения
     */
    @Override
    public Map<ReservationPlace, List<LocalDate>> getAllAvailablePlaceForReservations() {
        List<ReservationPlace> reservationPlaces = reservationPlaceDAO.getAllReservationPlaces();
        Map<ReservationPlace, List<LocalDate>> availableReservations = new HashMap<>();
        for(ReservationPlace reservationPlace : reservationPlaces) {
            List<LocalDate> allAvailableDatesForReservePlace = getAllAvailableDatesForReservePlace(reservationPlace);
            availableReservations.put(reservationPlace, allAvailableDatesForReservePlace);
        }
        return availableReservations;
    }

    /**
     * Метод для получения всех свободных дат для бронирования места
     * @param reservationPlace место для бронирования
     * @return коллекция доступных дат для бронирования указанного помещения
     */
    @Override
    public List<LocalDate> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace) {
        List<Reservation> allReservations = reservationDAO.getAllReservationsWithReservationPlace(reservationPlace.getId());
        List<LocalDate> availableDates = ReservationDateTimeGenerator.generateDates();
        for (Reservation reservation : allReservations) {
            if(getAllAvailableTimesForReservation(reservationPlace, reservation.getStartDate().toLocalDate()).isEmpty()) {
                availableDates.remove(reservation.getStartDate().toLocalDate());
            }
        }
        return availableDates;
    }

    /**
     * Метод для получения всех свободных временных промежутков для бронирования места
     * в виде ключа=значение в качестве одиночных слотов длительностью в 1 час, например (08:00=09:00, 09:00-10:00)
     * @param reservationPlace место для бронирования
     * @param localDate день бронирования
     * @return коллекция доступных промежутков времени для бронирования указанного помещения в указанную дату
     */
    @Override
    public Map<LocalTime, LocalTime> getAllAvailableTimesForReservation(ReservationPlace reservationPlace, LocalDate localDate) {
        Map<LocalTime, LocalTime> availableTimes = ReservationDateTimeGenerator.generateTimesPeriod();
        List<Reservation> allReservations =
                reservationDAO.getAllReservationsWithReservationPlace(reservationPlace.getId())
                        .stream()
                        .filter(x -> x.getStartDate().toLocalDate().isEqual(localDate))
                        .toList();
        for (Reservation reservation : allReservations) {
            long durationInHours = Duration.between(reservation.getStartDate(), reservation.getEndDate()).toHours();
            for(int i = 0; i < durationInHours; i++) {
                availableTimes.remove(reservation.getStartDate().toLocalTime().plusHours(i));
            }
        }
        return availableTimes;
    }

    @Override
    public List<String> getAllAvailableTimeListForReservation(ReservationPlace reservationPlace, LocalDate localDate) {
        Map<LocalTime, LocalTime> availableTimes = getAllAvailableTimesForReservation(reservationPlace, localDate);
        List<String> allAvailableTimes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (LocalTime endTime : availableTimes.values()) {
            LocalTime startTime = endTime.minusHours(1);
            if(startTime.getHour() + 1 == endTime.getHour()) {
               allAvailableTimes.add(startTime.format(dateTimeFormatter) + "-" + endTime.format(dateTimeFormatter));
            }
        }
        return allAvailableTimes;
    }

    @Override
    public List<ReservationPlace> getAllAvailablePlacesForReserveDate(LocalDate localDate) {
        Map<ReservationPlace, List<LocalDate>> availableReservationsWithDates = getAllAvailablePlaceForReservations();
        List<ReservationPlace> availableReservations = new ArrayList<>();
        for(ReservationPlace reservationPlace : reservationPlaceDAO.getAllReservationPlaces()) {
            for(int i = 0; i < availableReservationsWithDates.get(reservationPlace).size(); i++) {
                if (localDate.isEqual(availableReservationsWithDates.get(reservationPlace).get(i))) {
                    availableReservations.add(reservationPlace);
                }
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(availableReservations));
    }
}
