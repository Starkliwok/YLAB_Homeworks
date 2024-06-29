package com.Y_LAB.homework.in.user_panel.auth_panel;

import com.Y_LAB.homework.exception.user.auth.*;
import com.Y_LAB.homework.in.user_panel.HomePanel;
import com.Y_LAB.homework.in.util.ConsoleReader;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;


/**
 * Класс для вывода панели для регистрации пользователя и взаимодействия с ней
 * @author Денис Попов
 * @version 2.0
 */
public class RegistrationPanel {

    /** Поле сервиса пользователей, предназначенное взаимодействия с пользователями*/
    private final UserService userService;

    /** Статическое поле домашней панели, предназначенное для вывода информации новым пользователям*/
    private static final HomePanel homePanel = new HomePanel();

    public RegistrationPanel() {
        userService = new UserServiceImpl();
    }

    /**
     * Метод для вывода информации по регистрации пользователя,
     * считывает введённый логин пользователя, вызывает метод
     * {@link RegistrationPanel#createNewUser(String)} с параметром логина.
     * Метод работает рекурсивно, в случае если пользователь с указанным логином уже существует или логин
     * не соответствует формату
     */
    public void signUp() {
        String username;
        System.out.print("Введите логин: ");
        username = ConsoleReader.readStringValue();
        createNewUser(username);
    }

    /**
     * Метод для вывода информации по регистрации пользователя,
     * считывает введённый пароль пользователя с помощью {@link ConsoleReader#readStringValue()}, повторно
     * считывает введённый пароль пользователя для избежаний опечатки со стороны пользователя,
     * в случае несоответствия 2-х паролей или формата пароля выводится сообщение об ошибке и метод вызывается -
     * рекурсивно. Вызывает метод {@link UserService#saveUser(String, String)} для создания нового пользователя.
     * В случае успешного создания пользователя выводится сообщение, а так же вызывается метод домашней страницы
     * пользователя {@link HomePanel#printStartPage()}
     * @param username логин будущего пользователя
     */
    private void createNewUser(String username) {
        String password;
        try {
            System.out.print("Введите пароль: ");
            password = ConsoleReader.readStringValue();
            System.out.print("Повторите пароль: ");
            String repeatedPassword = ConsoleReader.readStringValue();
            if(!password.equals(repeatedPassword)) {
                throw new PasswordsDoNotMatchException("Пароли не совпадают, повторите попытку\n");
            }
            userService.saveUser(username, password);
        } catch (RegistrationException ex) {
            System.out.println(ex.getMessage());
            createNewUser(username);
        }
        System.out.println("\nВы успешно создали аккаунт");
        homePanel.printStartPage();
    }
}
