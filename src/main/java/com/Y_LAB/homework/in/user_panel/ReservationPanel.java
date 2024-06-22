package com.Y_LAB.homework.in.user_panel;

import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.exception.reservation.DatesForReservationDoesNotExistsException;
import com.Y_LAB.homework.exception.reservation.ReservationPeriodException;
import com.Y_LAB.homework.in.util.ConsoleReader;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Класс для вывода панели бронирования помещений
 * @author Денис Попов
 * @version 1.0
 */
public class ReservationPanel {

    /** Статическое поле панели пользователя, предназначенное взаимодействия с выбором пользователя*/
    private final UserPanel userPanel;

    /** Поле сервиса мест для бронирования, предназначенное взаимодействия с местами для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса бронирований, предназначенное взаимодействия с бронированиями*/
    private final ReservationService reservationService;

    public ReservationPanel() {
        userPanel = new UserPanel();
        reservationPlaceService = new ReservationPlaceServiceImpl();
        reservationService = new ReservationServiceImpl();
    }

    /**
     * Метод для создания бронирования вызывает метод {@link ReservationPanel#chooseReservationPlace()} для
     * выбора типа помещения, вызывает метод
     * {@link ReservationPanel#chooseDateForReservation(User, Reservation, ReservationPlace)} для выбора даты
     * бронирования, после чего сохраняет объект брони
     * @param user Объект пользователя
     */
    public void createReservation(User user) {
        ReservationPlace reservationPlace = chooseReservationPlace();
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReservationPlace(reservationPlace);
        try {
            chooseDateForReservation(user, reservation, reservationPlace);
        } catch (ReservationPeriodException e) {
            System.out.println(e.getMessage());
            userPanel.printUserPage(user);
        }

        reservationService.addReservation(reservation);
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
     * Метод для отображения и выбора доступного промежутка времени для бронирования места.
     * Метод выводит все доступные даты для бронирования указанного места, после чего пользователь
     * выбирает одну из указанных дат и на основе его выбора отображаются все доступные временные промежутки
     * для данной даты и данного места, после чего пользователь выбирает время начала и время окончания брони.
     * Во время отображение свободных промежутков времени проверяется возможность каждого времени на создание брони.
     * Вызывает {@link ReservationPanel#printAllAvailableDatesForReservation(List)} для отображения доступных дат.
     * Вызывает {@link ReservationPanel#printAllAvailableTimeForReservationInDate(List, User)} для отображения
     * доступных промежутков времени
     * @throws ReservationPeriodException Если место невозможно забронировать в данный промежуток времени
     * @param user пользователь
     * @param reservation бронь
     * @param reservationPlace место брони
     */
    private void chooseDateForReservation(User user, Reservation reservation, ReservationPlace reservationPlace) throws ReservationPeriodException {
        reservationService.getAllReservations().remove(reservation);
        List<Date> allAvailableDates = reservationPlaceService.getAllAvailableDatesForReservePlace(reservationPlace);
        try {
            printAllAvailableDatesForReservation(allAvailableDates);
        } catch (DatesForReservationDoesNotExistsException ex) {
            System.out.println(ex.getMessage());
            userPanel.printUserPage(user);
        }
        System.out.println("\nВведите дату брони");
        Date reservationDate = ConsoleReader.enterDate();
        LocalDate localReservationDate = LocalDate.ofInstant(reservationDate.toInstant(), ZoneId.systemDefault());
        List<Date> availableTimesInDate = allAvailableDates
                .stream()
                .filter(x -> LocalDate.ofInstant(x.toInstant(), ZoneId.systemDefault()).equals(localReservationDate))
                .toList();
        printAllAvailableTimeForReservationInDate(availableTimesInDate, user);

        System.out.println("\nВведите час начала брони");
        LocalDateTime localStartDateTime = ConsoleReader.enterTimeToDate(reservationDate);
        System.out.println("Введите час окончания брони");
        LocalDateTime localEndDateTime = ConsoleReader.enterTimeToDate(reservationDate);

        Date startDate = Date.from(localStartDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(localEndDateTime.atZone(ZoneId.systemDefault()).toInstant());
        int availableStartTime = LocalDateTime.ofInstant(availableTimesInDate.get(0).toInstant(), ZoneId.systemDefault()).getHour();
        int availableEndTime = LocalDateTime.ofInstant(availableTimesInDate.get(availableTimesInDate.size() - 1)
                .toInstant(), ZoneId.systemDefault()).getHour();
        if(localEndDateTime.getHour() <= localStartDateTime.getHour()
                || localEndDateTime.getHour() > availableEndTime
                || localEndDateTime.getHour() < availableStartTime
                || localStartDateTime.getHour() < availableStartTime) {
            throw new ReservationPeriodException("Место невозможно забронировать в данный промежуток времени");
        }
        List<Date> periodOfReservation = availableTimesInDate.stream()
                .filter(x -> x.getTime() >= startDate.getTime() && x.getTime() <= endDate.getTime()).toList();
        int i = 0;
        for(Date date : periodOfReservation) {
            int availableHour = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getHour();
            if(availableHour != localStartDateTime.getHour() + i++) {
                throw new ReservationPeriodException("Место невозможно забронировать в данный промежуток времени");
            }
        }
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
    }

    /**
     * Метод отображает свободные промежутки времени в один и тот же день для бронирования
     * @param availableTimesInDate пользователь
     * @param user пользователь
     */
    private void printAllAvailableTimeForReservationInDate(List<Date> availableTimesInDate, User user) {
        if(availableTimesInDate.isEmpty()) {
            System.out.println("За эту дату невозможно забронировать место");
            userPanel.printUserPage(user);
        }
        System.out.println("Свободное время для бронирования");
        for (Date date : availableTimesInDate) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
            System.out.print(localDate.format(dateTimeFormatter) + " ");
        }
    }

    /**
     * Метод отображает свободные дни для бронирования мест
     * @throws DatesForReservationDoesNotExistsException В случае если нет дат для бронирования места
     * @param allAvailableDates пользователь
     */
    private void printAllAvailableDatesForReservation(List<Date> allAvailableDates) throws DatesForReservationDoesNotExistsException {
        if(allAvailableDates.isEmpty()) {
            throw new DatesForReservationDoesNotExistsException("Свободных дат для бронирования нет");
        }
        System.out.println("Свободные даты для бронирования");
        List<String> allAvailableDatesInString = new ArrayList<>();
        for (Date date : allAvailableDates) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            allAvailableDatesInString.add(localDate.format(dateTimeFormatter));
        }
        List<String> uniqueDatesWithoutTime = new ArrayList<>(new LinkedHashSet<>(allAvailableDatesInString));
        uniqueDatesWithoutTime.forEach(System.out::println);
    }

    /**
     * Метод отображает места для бронирования и присваивает им порядковые номера, которые необходимы для выбора
     * места пользователем
     * @return Место для бронирования
     */
    private ReservationPlace chooseReservationPlace() {
        List<ReservationPlace> allReservationPlaces = reservationPlaceService.getAllReservationPlaces();
        for(int i = 0; i < allReservationPlaces.size(); i++) {
            System.out.println(i + 1 + ":" + allReservationPlaces.get(i) + "\n");
        }
        System.out.print("Выберите номер помещения для бронирования: ");
        int placeNumber = ConsoleReader.enterIntValue();
        if(placeNumber < 1 || placeNumber > allReservationPlaces.size()) {
            System.out.println("Помещения с таким номером не существует, повторите попытку");
            chooseReservationPlace();
        }
        System.out.println();
        return reservationPlaceService.getReservationPlace(placeNumber);
    }
}
