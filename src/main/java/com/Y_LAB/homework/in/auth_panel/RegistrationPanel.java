package com.Y_LAB.homework.in.auth_panel;

import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.exception.auth.PasswordFormatException;
import com.Y_LAB.homework.exception.auth.PasswordsDoNotMatchException;
import com.Y_LAB.homework.exception.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.auth.UsernameFormatException;
import com.Y_LAB.homework.in.user_panel.HomePanel;
import com.Y_LAB.homework.in.util.ConsoleReader;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;


/**
 * Класс для вывода панели для регистрации пользователя и взаимодействия с ней
 * @author Денис Попов
 * @version 1.0
 */
public class RegistrationPanel {

    /** Поле сервиса пользователей, предназначенное взаимодействия с пользователями*/
    private final UserService userService;

    /** Статическое поле домашней панели, предназначенное авторизации и регистрации новых пользователей*/
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
        try {
            System.out.print("Введите логин: ");
            username = ConsoleReader.enterUsername();
            createNewUser(username);
        } catch (UserAlreadyExistsException | UsernameFormatException ex) {
            System.out.println(ex.getMessage());
            signUp();
        }
    }

    /**
     * Метод для вывода информации по регистрации пользователя,
     * считывает введённый пароль пользователя с помощью {@link ConsoleReader#enterPassword()}, повторно
     * считывает введённый пароль пользователя для избежаний опечатки со стороны пользователя,
     * в случае несоответствия 2-х паролей или формата пароля выводится сообщение об ошибке и метод вызывается -
     * рекурсивно. Вызывает метод {@link UserService#addUserToUserSet(User)} для создания нового пользователя.
     * В случае успешного создания пользователя выводится сообщение, а так же вызывается метод домашней страницы
     * пользователя {@link HomePanel#printStartPage()}
     * @param username логин будущего пользователя
     */
    private void createNewUser(String username) {
        String password;
        try {
            System.out.print("Введите пароль: ");
            password = ConsoleReader.enterPassword();
            System.out.print("Повторите пароль: ");
            ConsoleReader.repeatPassword(password);
            User user = new User(username, password);
            userService.addUserToUserSet(user);
        } catch (PasswordsDoNotMatchException | PasswordFormatException ex) {
            System.out.println(ex.getMessage());
            createNewUser(username);
        }
        System.out.println("\nВы успешно создали аккаунт");
        homePanel.printStartPage();
    }
}
