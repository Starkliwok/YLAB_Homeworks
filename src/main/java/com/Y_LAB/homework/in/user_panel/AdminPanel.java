package com.Y_LAB.homework.in.user_panel;

import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.exception.admin.NotEnoughAdminRightsException;
import com.Y_LAB.homework.exception.reservation.ReservationDoesNotExistsException;
import com.Y_LAB.homework.exception.user.UserDoesNotExistsException;
import com.Y_LAB.homework.exception.user.auth.PasswordFormatException;
import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.user.auth.UsernameFormatException;
import com.Y_LAB.homework.in.util.ConsoleReader;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;

import java.util.List;

/**
 * Класс для вывода панели администратора и взаимодействия с ней
 * @author Денис Попов
 * @version 2.0
 */
public class AdminPanel {

    /** Поле сервиса пользователей, предназначенное взаимодействия с пользователями*/
    private final UserService userService;

    /** Поле сервиса мест для бронирования, предназначенное взаимодействия с местами для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса бронирований, предназначенное взаимодействия с бронированиями*/
    private final ReservationService reservationService;

    /** Статическое поле домашней панели, предназначенное для вывода информации новым пользователям*/
    private static final HomePanel homePanel = new HomePanel();

    /** Статическое поле панели пользователя, предназначенное взаимодействия с выбором пользователя*/
    private static final UserPanel userPanel = new UserPanel();

    public AdminPanel() {
        userService = new UserServiceImpl();
        reservationPlaceService = new ReservationPlaceServiceImpl();
        reservationService = new ReservationServiceImpl();
    }

    /**
     * Метод для вывода информации возможных действий администратора,
     * вызывает метод {@link AdminPanel#adminPageChooseAction(Admin)}
     * @param admin Объект администратора.
     */
    public void printAdminPage(Admin admin) {
        System.out.println("""
                 //Панель администратора//
                 Выберите действие:
                 1 - Посмотреть список пользователей
                 2 - Получить возможности пользователя
                 3 - Панель вывода пользовательских броней
                 4 - Добавить новое место для бронирования
                 5 - Удалить место для бронирования
                 6 - Редактировать место для бронирования
                 0 - Выход из Аккаунта""");
        adminPageChooseAction(admin);
    }

    /**
     * Метод для выбора действия администратора <br>
     * 1 - {@link AdminPanel#showAllUsers(Admin)} Посмотреть список пользователей<br>
     * 2 - {@link UserPanel#printUserPage(User)} Получить возможности пользователя<br>
     * 3 - {@link AdminPanel#chooseSortForReservations(Admin)} Отфильтровать бронирования пользователей<br>
     * 4 - {@link AdminPanel#createReservationPlace(Admin)} Добавить новое место для бронирования<br>
     * 5 - {@link AdminPanel#deleteReservationPlace(Admin)} Удалить место для бронирования<br>
     * 6 - {@link AdminPanel#updateReservationPlace(Admin)} Редактировать место для бронирования<br>
     * 0 - {@link HomePanel#printStartPage()} Выход из Аккаунта<br>
     * любое другое значение выводит сообщение об ошибке и рекурсивно вызывает метод
     * @param admin Объект администратора.
     */
    private void adminPageChooseAction(Admin admin) {
        switch (ConsoleReader.readPageChoose()) {
            case 1 ->
                    showAllUsers(admin);
            case 2 ->
                    userPanel.printUserPage(admin);
            case 3 ->
                    chooseSortForReservations(admin);
            case 4 ->
                    createReservationPlace(admin);
            case 5 ->
                    deleteReservationPlace(admin);
            case 6 ->
                    updateReservationPlace(admin);
            case 0 ->
                    homePanel.printStartPage();
            default -> {
                System.out.println("Некорректный ввод данных, повторите попытку");
                adminPageChooseAction(admin);
            }
        }
    }

    /**
     * Метод выводит информацию о пользователях, а так же считывает выбор действия администратора с пользователями<br>
     * 1 - {@link AdminPanel#chooseUserToInteract(Admin)} Перейти к выбору пользователя<br>
     * 0 - {@link UserPanel#printUserPage(User)} Назад <br>
     * любое другое значение выводит сообщение об ошибке и вызывает метод <br> {@link AdminPanel#printAdminPage(Admin)}
     * @param admin Объект администратора.
     */
    private void showAllUsers(Admin admin) {
        userService.getAllUsers().forEach(System.out::println);
        System.out.println("""
                 \nВыберите действие:
                 1 - Выбрать пользователя для взаимодействия с его аккаунтом
                 0 - Назад""");
        switch (ConsoleReader.readPageChoose()) {
            case 1 -> {
                try {
                    chooseUserToInteract(admin);
                } catch (UserDoesNotExistsException | NotEnoughAdminRightsException e) {
                    System.out.println(e.getMessage());
                    printAdminPage(admin);
                }
            }
            case 0 ->
                    printAdminPage(admin);
            default -> {
                System.out.println("Некорректный ввод данных, повторите попытку");
                showAllUsers(admin);
            }
        }
    }

    /**
     * Метод предназначен для выбора пользователя и взаимодействии с ним.
     * Считывает введенный id пользователя администратором, получает объект пользователя основываясь на id,
     * в случае если пользователя с таким id не существует, то вызывается метод {@link AdminPanel#showAllUsers(Admin)}.<br>
     * Если же пользователь существует, то он проверяется на принадлежность к администратору, поскольку администратор
     * может взаимодействовать с пользователями, но администратор не может взаимодействовать с другим администратором,
     * в случае несоответствия вызывается метод {@link AdminPanel#showAllUsers(Admin)} <br>
     * в случае если администратор выбирает самого себя для взаимодействия. <br>
     * вызывается метод {@link AdminPanel#chooseActionWithUser(User, Admin)}
     * @param admin Объект администратора.
     * @throws UserDoesNotExistsException Если пользователя с таким id не существует
     * @throws NotEnoughAdminRightsException Если недостаточно прав администратора для совершения действия
     */
    private void chooseUserToInteract(Admin admin) throws UserDoesNotExistsException, NotEnoughAdminRightsException {
        System.out.println("Введите id пользователя для взаимодействия или любое другое число для выхода");
        int id = ConsoleReader.readIntValue();
        User user = userService.getUser(id);
        if(user == null) {
            throw new UserDoesNotExistsException("Пользователя с таким id не существует");
        } else if(user instanceof Admin && id != admin.getId()) {
            throw new NotEnoughAdminRightsException("Администратор не имеет доступа к изменению данных аккаунта другого администратора");
        }
        chooseActionWithUser(user, admin);
    }

    /**
     * Метод для выбора действия администратора с переданным пользователем <br>
     * 1 - используется для изменения логина пользователя, считывается новый логин, проверяется на наличие такого логина
     * у другого пользователя, в случае если логин не зарезервирован, то вызывается метод <br>
     * {@link UserService#updateUser(User)} для изменения логина, после изменения метод рекурсивно вызывается
     * для дальнейший действий с пользователем со стороны администратора <br>
     * 2 - используется для изменения пароля пользователя, считывается новый пароль вызывается метод <br>
     * {@link UserService#updateUser(User)} для изменения пароля, после изменения метод работает аналогично
     * как и метод по изменению логина <br>
     * 3 - вызывается метод {@link UserService#deleteUser(long)} в случае если указанный пользователь является
     * администратором, то удаляется его собственный аккаунт, почему так происходит описано в <br>
     * {@link AdminPanel#chooseUserToInteract(Admin)} в случае если пользователь не является администратором,
     * он так же удаляется <br>
     * 4 - {@link UserPanel#printUserPage(User)} используется для взаимодействием с дневником тренировок
     * выбранного пользователя<br>
     * 0 - {@link AdminPanel#showAllUsers(Admin)} возвращает на домашнюю страницу<br>
     * любое другое значение выводит сообщение об ошибке и рекурсивно вызывает метод
     * @param user пользователь, с которым происходит взаимодействие
     * @param admin Объект администратора.
     */
    private void chooseActionWithUser(User user, Admin admin) {
        System.out.println("""
                Выберите действие:
                1 - Изменить логин пользователя
                2 - Изменить пароль пользователя
                3 - Удалить пользователя
                4 - Действия с бронированием мест пользователя\s
                0 - Назад""");
        switch (ConsoleReader.readPageChoose()) {
            case 1 -> {
                System.out.println("Введите новый логин");
                String newName = ConsoleReader.readStringValue();
                try {
                    userService.checkUserLogin(newName);
                } catch (UsernameFormatException | UserAlreadyExistsException e) {
                    System.out.println(e.getMessage());
                    chooseActionWithUser(user, admin);
                }
                user.setUsername(newName);
                userService.updateUser(user);
                System.out.println("Вы успешно изменили логин пользователя");
                chooseActionWithUser(user, admin);
            }
            case 2 -> {
                System.out.println("Введите новый пароль");
                String newPassword = ConsoleReader.readStringValue();
                try {
                    userService.checkUserPassword(newPassword);
                } catch (PasswordFormatException e) {
                    System.out.println(e.getMessage());
                    chooseActionWithUser(user, admin);
                }
                user.setPassword(newPassword);
                userService.updateUser(user);
                System.out.println("Вы успешно изменили пароль пользователя");
                chooseActionWithUser(user, admin);
            }
            case 3 -> {
                userService.deleteUser(user.getId());
                if (user instanceof Admin) {
                    System.out.println("Вы удалили свой аккаунт");
                    homePanel.printStartPage();
                }
                System.out.println("Вы успешно удалили пользователя");
                printAdminPage(admin);
            }
            case 4 ->
                userPanel.printUserPage(user);
            case 0 ->
                showAllUsers(admin);
            default -> {
                System.out.println("Некорректный ввод данных, повторите попытку");
                chooseActionWithUser(user, admin);
            }
        }
    }

    /**
     * Метод для выбора сортировки и отображения отсортированных данных
     * 1 - Вывод бронирований отсортированных по дате<br>
     * 2 - Вывод бронирований отсортированных по пользователям<br>
     * 3 - Вывод бронирований всех бронирований без сортировки<br>
     * 4 - Вывод мест для бронирований отсортированных по типу помещения<br>
     * 0 - {@link AdminPanel#printAdminPage(Admin)} Выход на главную панель администратора<br>
     * @param admin Объект администратора.
     */
    private void chooseSortForReservations(Admin admin) {
        System.out.println("""
                Выберите способ фильтрации:
                1 - Отсортировать брони по дате бронирования
                2 - Отсортировать брони по пользователям
                3 - Вывести все брони без фильтрации
                4 - Отсортировать места по типу помещения
                Любое другое число - Назад""");
        switch (ConsoleReader.readPageChoose()) {
            case 1 -> {
                for (Reservation reservation : reservationService.getAllReservationsByDate()) {
                    System.out.println(reservation + "\n");
                }
                printAdminPage(admin);
            }
            case 2 -> {
                for(Reservation reservation : reservationService.getAllReservationsByUsers()) {
                    System.out.println(reservation + "\n");
                }
                printAdminPage(admin);
            }
            case 3 -> {
                for (Reservation reservation : reservationService.getAllReservations()){
                    System.out.println(reservation + "\n");
                }
                printAdminPage(admin);
            }
            case 4 -> {
                for (ReservationPlace reservationPlace : reservationPlaceService.getAllReservationPlacesByTypes(chooseTypeOfReservationPlace())){
                    System.out.println(reservationPlace + "\n");
                }
                printAdminPage(admin);
            }
            default ->
                printAdminPage(admin);
        }
    }

    /**
     * Метод для создания нового места для бронирования вызывает {@link AdminPanel#chooseTypeOfReservationPlace()}
     * для выбора типа помещения, вызывает {@link AdminPanel#enterValuesForReservationPlace(ReservationPlace)}
     * для заполнения полей будущего места, после чего сохраняет новое место и вызывает панель администратора
     * @param admin Объект администратора.
     */
    private void createReservationPlace(Admin admin) {
        ReservationPlace reservationPlace = chooseTypeOfReservationPlace();
        if(reservationPlace != null) {
            enterValuesForReservationPlace(reservationPlace);
            reservationPlaceService.saveReservationPlace(reservationPlace);
            System.out.println("Вы успешно создали место для аренды");
        }
        printAdminPage(admin);
    }

    /**
     * Метод заполняет поля места для бронирования данными, которые вводит пользователь
     * @param reservationPlace Объект места для бронирования.
     */
    private void enterValuesForReservationPlace(ReservationPlace reservationPlace) {
        System.out.print("\nВведите площадь места в метрах: ");
        reservationPlace.setPlaceArea(ConsoleReader.readDoubleValue());
        System.out.print("\nВведите название места: ");
        reservationPlace.setName(ConsoleReader.readStringValue());
        System.out.print("\nВведите стоимость аренды места за сутки в долларах: ");
        reservationPlace.setCostPerHour(ConsoleReader.readDoubleValue());
        System.out.print("\nВведите количество сидений: ");
        reservationPlace.setNumberOfSeats(ConsoleReader.readIntValue());
    }

    /**
     * Метод для выбора типа места для бронирования
     * 1 - Рабочее место {@link Workplace}<br>
     * 2 - Конференц-зал {@link ConferenceRoom}<br>
     * @return объект соответствующего помещения, в случае если номер не соответствует типу объекта, возвращается null
     */
    private ReservationPlace chooseTypeOfReservationPlace() {
        System.out.println("""
                Выберите тип помещения для бронирований:
                1 - Рабочее место
                2 - Конференц-зал
                Любое другое число - Выход в главное меню""");
        switch (ConsoleReader.readPageChoose()) {
            case 1 -> {
                return new Workplace();
            }
            case 2 -> {
                return new ConferenceRoom();
            }
        }
        return null;
    }

    /**
     * Метод для удаления места для бронирования, вызывает {@link AdminPanel#chooseReservationPlace()} для выбора
     * места, которое подлежит удалению, после чего удаляет место из памяти приложения
     * @param admin объект администратора
     */
    private void deleteReservationPlace(Admin admin) {
        try {
            ReservationPlace reservationPlace = chooseReservationPlace();
            reservationPlaceService.deleteReservationPlace(reservationPlace.getId());
        } catch (ReservationDoesNotExistsException e) {
            System.out.println(e.getMessage());
            printAdminPage(admin);
        }
        System.out.println("Вы успешно удалили место");
        printAdminPage(admin);
    }

    /**
     * Метод для выбора места для бронирования, метод получает список всех существующий объектов и отображает их
     * вместе с порядковым номером, после чего получает порядковый номер места от выбора пользователя
     * @return объект выбранного места для бронирования
     * @throws ReservationDoesNotExistsException если места с выбранным номером не существует
     */
    private ReservationPlace chooseReservationPlace() throws ReservationDoesNotExistsException {
        List<ReservationPlace> allReservationPlaces = reservationPlaceService.getAllReservationPlaces();
        for (ReservationPlace reservationPlace : allReservationPlaces) {
            System.out.println(reservationPlace.getId() + "" + reservationPlace + "\n");
        }
        System.out.println("Выберите номер места");
        int placeNumber = ConsoleReader.readPageChoose();
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(placeNumber);
        if(reservationPlace == null) {
            throw new ReservationDoesNotExistsException("Места с таким номером не существует");
        }
        return reservationPlace;
    }

    /**
     * Метод для изменения места для бронирования, вызывает {@link AdminPanel#chooseReservationPlace()} для выбора
     * места, которое подлежит изменению, вызывает {@link AdminPanel#enterValuesForReservationPlace(ReservationPlace)}
     * для заполнения полей новыми данными, после чего сохраняет объект в памяти приложения
     * @param admin объект администратора
     */
    private void updateReservationPlace(Admin admin) {
        System.out.println("Выберите номер места для изменения");
        try {
            ReservationPlace reservationPlace = chooseReservationPlace();
            enterValuesForReservationPlace(reservationPlace);
            reservationPlaceService.updateReservationPlace(reservationPlace);
        } catch (ReservationDoesNotExistsException e) {
            System.out.println(e.getMessage());
            printAdminPage(admin);
        }
        System.out.println("Вы успешно изменили место");
        printAdminPage(admin);
    }
}