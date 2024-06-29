package com.Y_LAB.homework.in.user_panel;

import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.roles.Admin;
import com.Y_LAB.homework.entity.roles.User;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс для вывода панели пользователя
 * @author Денис Попов
 * @version 2.0
 */
public class UserPanel {

    /** Поле сервиса мест для бронирования, предназначенное взаимодействия с местами для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса бронирований, предназначенное взаимодействия с бронированиями*/
    private final ReservationService reservationService;

    /** Статическое поле панели администратора*/
    private static final AdminPanel adminPanel = new AdminPanel();

    /** Статическое поле домашней панели, предназначенное для вывода информации новым пользователям*/
    private static final HomePanel homePanel = new HomePanel();

    /** Статическое поле панели бронирований, предназначенное взаимодействия с бронированиями*/
    private static final ReservationPanel reservationPanel = new ReservationPanel();

    private final FreeReservationSlot freeReservationSlot;

    public UserPanel() {
        reservationPlaceService = new ReservationPlaceServiceImpl();
        reservationService = new ReservationServiceImpl();
        freeReservationSlot = new FreeReservationSlot();
    }

    /**
     * Метод выводит информацию о возможных действиях пользователя, а так же дополнительную информацию для 
     * администратора, который использует панель как обычный пользователь
     * @param user объект пользователя
     */
    public void printUserPage(User user) {
        System.out.print("""
                 Выберите действие:
                 1 - Посмотреть список доступных мест
                 2 - Посмотреть список доступных мест за дату
                 3 - Посмотреть ваш список забронированных мест
                 4 - Забронировать место
                 5 - Отменить бронь
                 6 - Редактировать дату бронирования
                 0 - Выход из аккаунта""");
        if(user instanceof Admin) {
            System.out.println(" пользователя и переход в панель администратора");
        }
        System.out.println();
        userPageChooseAction(user);
    }

    /**
     * Метод считывает ввод пользователя и на его основе вызывает методы <br>
     * 1 - {@link UserPanel#printAllAvailablePlaces()} - Посмотреть список доступных мест<br>
     * 2 - {@link UserPanel#printAllAvailablePlacesForDateReserve()} - Посмотреть список доступных мест за дату<br>
     * 3 - {@link UserPanel#printAllUserReservations(long)} - Посмотреть ваш список забронированных мест<br>
     * 4 - {@link ReservationPanel#createReservation(User)} - Забронировать место<br>
     * 5 - {@link UserPanel#cancelReservation(User)} - Отменить бронь<br>
     * 6 - {@link UserPanel#updateReservation(User)} - Редактировать дату бронирования<br>
     * 0 - Выход из аккаунта / В случае, если это администратор, то переводит его в панель администратора<br>
     * В других случаях выводит информацию о некорректном вводе данных и рекурсивно вызывает метод
     * @param user объект пользователя
     */
    private void userPageChooseAction(User user) {
        switch (ConsoleReader.readPageChoose()) {
            case 1 -> {
                printAllAvailablePlaces();
                printUserPage(user);
            }
            case 2 -> {
                printAllAvailablePlacesForDateReserve();
                printUserPage(user);
            }
            case 3 -> {
                printAllUserReservations(user.getId());
                printUserPage(user);
            }
            case 4 -> {
                reservationPanel.createReservation(user);
                printUserPage(user);
            }
            case 5 -> {
                try {
                    cancelReservation(user);
                } catch (ReservationDoesNotExistsException | ReservationPeriodException e) {
                    System.out.println(e.getMessage());
                }
                printUserPage(user);
            }
            case 6 -> {
                try {
                    updateReservation(user);
                } catch (ReservationDoesNotExistsException e) {
                    System.out.println(e.getMessage());
                }
                printUserPage(user);
            }
            case 0 -> {
                if (user instanceof Admin) {
                    adminPanel.printAdminPage((Admin) user);
                }
                homePanel.printStartPage();
            }
            default -> {
                System.out.println("Некорректный ввод данных, повторите попытку");
                userPageChooseAction(user);
            }
        }
    }

    /** Метод выводит все места для бронирования и свободные даты для бронирования этих мест*/
    private void printAllAvailablePlaces() {
        Map<ReservationPlace, List<LocalDate>> availableReservations = freeReservationSlot.getAllAvailablePlaceForReservations();
        for(ReservationPlace reservationPlace : reservationPlaceService.getAllReservationPlaces()) {
            System.out.println(reservationPlace);
            System.out.println("Свободные даты для бронирования:");
            for (LocalDate localDate : availableReservations.get(reservationPlace)) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
                System.out.print(localDate.format(dateTimeFormatter) + ", ");
            }
            System.out.println();
        }
    }

    /** Метод выводит все доступные места для бронирования в указанную дату*/
    private void printAllAvailablePlacesForDateReserve() {
        System.out.print("Введите дату в формате \"ДД-ММ-ГГГГ\":");
        LocalDate localDate = ConsoleReader.readDate();
        System.out.println();
        Map<ReservationPlace, List<LocalDate>> availableReservationsWithDates = freeReservationSlot.getAllAvailablePlaceForReservations();
        List<ReservationPlace> availableReservations = new ArrayList<>();
        for(ReservationPlace reservationPlace : reservationPlaceService.getAllReservationPlaces()) {
            for(int i = 0; i < availableReservationsWithDates.get(reservationPlace).size(); i++) {
                if (localDate.isEqual(availableReservationsWithDates.get(reservationPlace).get(i))) {
                    availableReservations.add(reservationPlace);
                }
            }
        }
        List<ReservationPlace> uniqueAvailableReservations = new ArrayList<>(new LinkedHashSet<>(availableReservations));
        uniqueAvailableReservations.forEach(System.out::println);
    }

    /** Метод выводит все брони пользователя
     * @param userId Уникальный идентификатор пользователя
     */
    private void printAllUserReservations(long userId) {
        List<Reservation> allUserReservations = reservationService.getAllUserReservations(userId);
        System.out.println();
        for (Reservation allUserReservation : allUserReservations) {
            System.out.println(allUserReservation.getId() + "" + allUserReservation + "\n");
        }
    }

    /** Метод отменяет бронь пользователя, выводит все брони пользователя и присваивает им порядковый номер, по которому
     * пользователь выбирает бронь для отмены
     * @throws ReservationDoesNotExistsException Если брони с таким номером не существует
     * @throws ReservationPeriodException Если невозможно создать бронь в указанный промежуток времени
     * @param user Объект пользователя
     */
    private void cancelReservation(User user) throws ReservationDoesNotExistsException, ReservationPeriodException {
        printAllUserReservations(user.getId());
        System.out.println("Выберите номер брони для отмены");
        int numberReservation = ConsoleReader.readIntValue();
        Reservation reservation = reservationService.getReservation(numberReservation);
        if(reservation == null) {
            throw new ReservationDoesNotExistsException("Брони с таким номером не существует");
        }
        if(reservation.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ReservationPeriodException("Эту бронь невозможно отменить");
        }
        System.out.println(reservation);
        System.out.println("""
                Вы уверены что хотите отменить эту бронь?
                 Выберите действие:
                 1 - Да
                 2 - Нет
                 Любое другое число - выход в главное меню""");
        switch (ConsoleReader.readPageChoose()) {
            case 1 -> {
                reservationService.deleteReservation(reservation.getId());
                System.out.println("Вы успешно отменили бронь");
            }
            case 2 ->
                cancelReservation(user);
            default ->
                printUserPage(user);
        }
    }

    /** Метод вызывает {@link UserPanel#printAllUserReservations(long)} для отображения
     * всех бронирований пользователя, после выбора брони вызывает
     * {@link ReservationPanel#updateReservation(User, Reservation)} для обновления выбранной брони
     * @throws ReservationDoesNotExistsException Если брони с таким номером не существует
     * @param user Объект пользователя
     */
    private void updateReservation(User user) throws ReservationDoesNotExistsException {
        printAllUserReservations(user.getId());
        System.out.println("Выберите номер брони для изменения");
        int numberReservation = ConsoleReader.readIntValue();
        Reservation reservation = reservationService.getReservation(numberReservation);
        if(reservation == null) {
            throw new ReservationDoesNotExistsException("Брони с таким номером не существует");
        }
        reservationPanel.updateReservation(user, reservation);
    }
}
