package com.Y_LAB.homework.in.user_panel;

import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.exception.reservation.DatesForReservationDoesNotExistsException;
import com.Y_LAB.homework.exception.reservation.ReservationDoesNotExistsException;
import com.Y_LAB.homework.exception.reservation.ReservationPeriodException;
import com.Y_LAB.homework.in.util.ConsoleReader;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import com.Y_LAB.homework.util.reservation.FreeReservationSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс для вывода панели бронирования помещений
 * @author Денис Попов
 * @version 2.0
 */
public class ReservationPanel {

    /** Поле сервиса мест для бронирования, предназначенное взаимодействия с местами для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса бронирований, предназначенное взаимодействия с бронированиями*/
    private final ReservationService reservationService;

    /** Статическое поле панели пользователя, предназначенное взаимодействия с выбором пользователя*/
    private static final UserPanel userPanel = new UserPanel();

    private final FreeReservationSlot freeReservationSlot;

    public ReservationPanel() {
        reservationPlaceService = new ReservationPlaceServiceImpl();
        reservationService = new ReservationServiceImpl();
        freeReservationSlot = new FreeReservationSlot();
    }

    /**
     * Метод для создания бронирования вызывает метод {@link ReservationPanel#chooseReservationPlace()} для
     * выбора типа помещения, вызывает метод
     * {@link ReservationPanel#chooseDateForReservation(User, Reservation, ReservationPlace)} для выбора даты
     * бронирования, после чего сохраняет объект брони
     * @param user Объект пользователя
     */
    public void createReservation(User user) {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        try {
            ReservationPlace reservationPlace = chooseReservationPlace();
            reservation.setReservationPlace(reservationPlace);
            chooseDateForReservation(user, reservation, reservationPlace);
        } catch (ReservationPeriodException | ReservationDoesNotExistsException e) {
            System.out.println(e.getMessage());
            userPanel.printUserPage(user);
        }
        reservationService.saveReservation(reservation);
        System.out.println("Вы успешно забронировали место");
        userPanel.printUserPage(user);
    }

    /**
     * Метод для обновления брони, вызывает метод
     * {@link ReservationPanel#chooseDateForReservation(User, Reservation, ReservationPlace)} для выбора свободных дат
     * бронирования, после чего сохраняет объект брони
     * @param user Объект пользователя
     * @param reservation Объект брони
     */
    public void updateReservation(User user, Reservation reservation) {
        ReservationPlace reservationPlace = reservation.getReservationPlace();
        try {
            chooseDateForReservation(user, reservation, reservationPlace);
        } catch (ReservationPeriodException e) {
            System.out.println(e.getMessage());
            userPanel.printUserPage(user);
        }
        reservationService.updateReservation(reservation);
        System.out.println("Вы успешно изменили дату брони");
        userPanel.printUserPage(user);
    }

    /**
     * Метод для выбора доступного промежутка времени для бронирования места.
     * Метод выводит все доступные даты для бронирования указанного места, после чего пользователь
     * выбирает одну из указанных дат и на основе его выбора отображаются все доступные временные промежутки
     * для данной даты и данного места, после чего пользователь выбирает время начала и время окончания брони.
     * Вызывает {@link ReservationPanel#printAllAvailableDatesForReservation(List)} для отображения доступных дат.
     * Вызывает {@link ReservationPanel#printAllAvailableTimesForReservationInDate(Map, User)} для отображения
     * доступных промежутков времени
     * @throws ReservationPeriodException Если место невозможно забронировать в данный промежуток времени
     * @param user пользователь
     * @param reservation бронь
     * @param reservationPlace место брони
     */
    private void chooseDateForReservation(User user, Reservation reservation, ReservationPlace reservationPlace) throws ReservationPeriodException {
        reservationService.deleteReservation(reservation.getId());
        List<LocalDate> allAvailableDates = freeReservationSlot.getAllAvailableDatesForReservePlace(reservationPlace);
        try {
            printAllAvailableDatesForReservation(allAvailableDates);
        } catch (DatesForReservationDoesNotExistsException ex) {
            System.out.println(ex.getMessage());
            userPanel.printUserPage(user);
        }
        System.out.print("\nВведите дату брони в формате \"ДД-ММ-ГГГГ\":");
        LocalDate reservationDate = ConsoleReader.readDate();
        Map<LocalTime, LocalTime> availableTimesInDate =
                freeReservationSlot.getAllAvailableTimesForReservePlace(reservationPlace, reservationDate);
        printAllAvailableTimesForReservationInDate(availableTimesInDate, user);
        System.out.print("\nВведите час начала брони в формате \"ЧЧ\":");
        LocalDateTime startDateTime = LocalDateTime.of(reservationDate, ConsoleReader.readHour());
        System.out.print("\nВведите час окончания брони в формате \"ЧЧ\":");
        LocalDateTime endDateTime = LocalDateTime.of(reservationDate, ConsoleReader.readHour());
        System.out.println();

        int availableStartHour = availableTimesInDate.values().stream().toList().get(0).getHour() - 1;
        int availableEndHour = availableTimesInDate.values().stream().toList().get(availableTimesInDate.size() - 1).getHour();
        if(endDateTime.getHour() <= startDateTime.getHour()
                || endDateTime.getHour() > availableEndHour
                || endDateTime.getHour() < availableStartHour
                || startDateTime.getHour() < availableStartHour) {
            throw new ReservationPeriodException("Место невозможно забронировать в данный промежуток времени");
        }
        for(int i = startDateTime.getHour(); i <= endDateTime.getHour(); i++) {
            if(!availableTimesInDate.containsKey(startDateTime.withHour(i).toLocalTime())
                    && availableTimesInDate.get(startDateTime.withHour(i - 1).toLocalTime()) == null) {
                    throw new ReservationPeriodException("Место невозможно забронировать в данный промежуток времени");
            }
        }
        reservation.setStartDate(startDateTime);
        reservation.setEndDate(endDateTime);
    }

    /**
     * Метод отображает свободные промежутки времени в один и тот же день для бронирования
     * @param availableTimes все доступные времена
     * @param user пользователь
     */
    private void printAllAvailableTimesForReservationInDate(Map<LocalTime, LocalTime> availableTimes, User user) {
        if(availableTimes.isEmpty()) {
            System.out.println("За эту дату невозможно забронировать место");
            userPanel.printUserPage(user);
        }
        System.out.println("Свободное время для бронирования");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (LocalTime endTime : availableTimes.values()) {
            LocalTime startTime = endTime.minusHours(1);
            if(startTime.getHour() + 1 == endTime.getHour()) {
                System.out.println(startTime.format(dateTimeFormatter) + "-" + endTime.format(dateTimeFormatter));
            }
        }
    }

    /**
     * Метод отображает свободные дни для бронирования мест
     * @throws DatesForReservationDoesNotExistsException В случае если нет дат для бронирования места
     * @param allAvailableDates все доступные даты
     */
    private void printAllAvailableDatesForReservation(List<LocalDate> allAvailableDates) throws DatesForReservationDoesNotExistsException {
        if(allAvailableDates.isEmpty()) {
            throw new DatesForReservationDoesNotExistsException("Свободных дат для бронирования нет");
        }
        System.out.println("Свободные даты для бронирования");
        List<String> allAvailableDatesInString = new ArrayList<>();
        for (LocalDate localDate : allAvailableDates) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            allAvailableDatesInString.add(localDate.format(dateTimeFormatter));
        }
        allAvailableDatesInString.forEach(System.out::println);
    }

    /**
     * Метод отображает места для бронирования и присваивает им порядковые номера, которые необходимы для выбора
     * места пользователем
     * @throws ReservationDoesNotExistsException Если места с указанным номером не существует
     * @return Место для бронирования
     */
    private ReservationPlace chooseReservationPlace() throws ReservationDoesNotExistsException {
        List<ReservationPlace> allReservationPlaces = reservationPlaceService.getAllReservationPlaces();
        for (ReservationPlace allReservationPlace : allReservationPlaces) {
            System.out.println(allReservationPlace.getId() + ":" + allReservationPlace + "\n");
        }
        System.out.print("Выберите номер помещения для бронирования: ");
        int placeNumber = ConsoleReader.readIntValue();
        System.out.println();
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(placeNumber);
        if(reservationPlace == null) {
            throw new ReservationDoesNotExistsException("Места с таким номером не существует");
        }
        return reservationPlace;
    }
}
