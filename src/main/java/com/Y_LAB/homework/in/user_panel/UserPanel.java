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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс для вывода панели пользователя
 * @author Денис Попов
 * @version 1.0
 */
public class UserPanel {

    /** Поле панели администратора*/
    private static final AdminPanel adminPanel = new AdminPanel();

    /** Поле домашней панели*/
    private static final HomePanel homePanel = new HomePanel();

    /** Статическое поле панели бронирований, предназначенное взаимодействия с бронированиями*/
    private static final ReservationPanel reservationPanel = new ReservationPanel();

    /** Поле сервиса мест для бронирования, предназначенное взаимодействия с местами для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса бронирований, предназначенное взаимодействия с бронированиями*/
    private final ReservationService reservationService;

    public UserPanel() {
        reservationPlaceService = new ReservationPlaceServiceImpl();
        reservationService = new ReservationServiceImpl();
    }

    /**
     * Метод выводит информацию о возможных действиях пользователя, а так же дополнительную информацию для 
     * администратора, который использует панель как обычный пользователь
     */
    public void printUserPage(User user) {
        System.out.print("""
                 Выберите действие:\s
                 1 - Посмотреть список доступных мест\s
                 2 - Посмотреть список доступных мест за дату\s
                 3 - Посмотреть ваш список забронированных мест\s
                 4 - Забронировать место\s
                 5 - Отменить бронь\s
                 6 - Редактировать дату бронирования\s
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
     * 3 - {@link UserPanel#printAllUserReservations(User)} - Посмотреть ваш список забронированных мест<br>
     * 4 - {@link ReservationPanel#createReservation(User)} - Забронировать место<br>
     * 5 - {@link UserPanel#cancelReservation(User)} - Отменить бронь<br>
     * 6 - {@link UserPanel#updateReservation(User)} - Редактировать дату бронирования<br>
     * 0 - Выход из аккаунта / В случае, если это администратор, то переводит его в панель администратора<br>
     * В других случаях выводит информацию о некорректном вводе данных и рекурсивно вызывает метод
     */
    private void userPageChooseAction(User user) {
        switch (ConsoleReader.PageChoose()) {
            case 1 -> {
                printAllAvailablePlaces();
                printUserPage(user);
            }
            case 2 -> {
                printAllAvailablePlacesForDateReserve();
                printUserPage(user);
            }
            case 3 -> {
                printAllUserReservations(user);
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
                    printUserPage(user);
                }
            }
            case 6 -> {
                try {
                    updateReservation(user);
                } catch (ReservationDoesNotExistsException e) {
                    System.out.println(e.getMessage());
                    printUserPage(user);
                }
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
        Map<ReservationPlace, List<Date>> availableReservations = reservationPlaceService.getAllAvailableReservations();
        for(ReservationPlace reservationPlace : reservationPlaceService.getAllReservationPlaces()) {
            System.out.println(reservationPlace);
            System.out.println("Свободные даты для бронирования:");
            for (Date date : availableReservations.get(reservationPlace)) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                System.out.print(localDate.format(dateTimeFormatter) + ", ");
            }
            System.out.println();
        }
    }

    /** Метод выводит все доступные места для бронирования в указанную дату*/
    private void printAllAvailablePlacesForDateReserve() {
        System.out.println("Введите дату");
        Date date = ConsoleReader.enterDate();
        Map<ReservationPlace, List<Date>> availableReservationsWithDates = reservationPlaceService.getAllAvailableReservations();
        List<ReservationPlace> availableReservations = new ArrayList<>();
        for(ReservationPlace reservationPlace : reservationPlaceService.getAllReservationPlaces()) {
            for(int i = 0; i < availableReservationsWithDates.get(reservationPlace).size(); i++) {
                LocalDate localDate = LocalDate.ofInstant(availableReservationsWithDates.get(reservationPlace).get(i).toInstant()
                        , ZoneId.systemDefault()).atStartOfDay().toLocalDate();
                if (date.equals(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    availableReservations.add(reservationPlace);
                }
            }
        }
        List<ReservationPlace> uniqueAvailableReservations = new ArrayList<>(new LinkedHashSet<>(availableReservations));
        uniqueAvailableReservations.forEach(System.out::println);
    }

    /** Метод выводит все брони пользователя
     * @param user Объект пользователя
     */
    private void printAllUserReservations(User user) {
        List<Reservation> allUserReservations = reservationService.getAllUserReservations(user);
        System.out.println();
        for(int i = 0; i < allUserReservations.size(); i++) {
            System.out.println(i + 1 + "" + allUserReservations.get(i) + "\n");
        }
    }

    /** Метод отменяет бронь пользователя, выводит все брони пользователя и присваивает им порядковый номер, по которому
     * пользователь выбирает бронь для отмены
     * @throws ReservationDoesNotExistsException Если брони с таким номером не существует
     * @throws ReservationPeriodException Если невозможно создать бронь в указанный промежуток времени
     * @param user Объект пользователя
     */
    private void cancelReservation(User user) throws ReservationDoesNotExistsException, ReservationPeriodException {
        List<Reservation> allUserReservations = reservationService.getAllUserReservations(user);
        printAllUserReservations(user);
        System.out.println("Выберите номер брони для отмены");
        int numberReservation = ConsoleReader.enterIntValue();
        if(numberReservation < 1 || numberReservation > allUserReservations.size()) {
            throw new ReservationDoesNotExistsException("Брони с таким номером не существует");
        }
        Reservation reservation = allUserReservations.get(numberReservation - 1);
        if(reservation.getEndDate().before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
            throw new ReservationPeriodException("Эту бронь невозможно отменить");
        }
        System.out.println(reservation);
        System.out.println("""
                Вы уверены что хотите отменить эту бронь?\s
                 Выберите действие:\s
                 1 - Да\s
                 2 - Нет\s
                 Любое другое число - выход в главное меню""");
        switch (ConsoleReader.PageChoose()) {
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

    /** Метод вызывает {@link UserPanel#printAllUserReservations(User)} для отображения
     * всех бронирований пользователя, после выбора брони вызывает
     * {@link ReservationPanel#updateReservation(User, Reservation)} для обновления выбранной брони
     * @throws ReservationDoesNotExistsException Если брони с таким номером не существует
     * @param user Объект пользователя
     */
    private void updateReservation(User user) throws ReservationDoesNotExistsException {
        List<Reservation> allUserReservations = reservationService.getAllUserReservations(user);
        printAllUserReservations(user);
        System.out.println("Выберите номер брони для изменения");
        int numberReservation = ConsoleReader.enterIntValue();
        if(numberReservation < 1 || numberReservation > allUserReservations.size()) {
            throw new ReservationDoesNotExistsException("Брони с таким номером не существует");
        }
        Reservation reservation = allUserReservations.get(numberReservation - 1);
        reservationPanel.updateReservation(user, reservation);
    }
}
