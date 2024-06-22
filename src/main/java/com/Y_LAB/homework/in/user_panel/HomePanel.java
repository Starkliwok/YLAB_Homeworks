package com.Y_LAB.homework.in.user_panel;

import com.Y_LAB.homework.in.auth_panel.AuthorizationPanel;
import com.Y_LAB.homework.in.auth_panel.RegistrationPanel;
import com.Y_LAB.homework.in.util.ConsoleReader;

/**
 * Класс для вывода панели с авторизацией и регистрацией
 * @author Денис Попов
 * @version 1.0
 */
public class HomePanel {

    /** Статическое поле панели авторизации, предназначенное авторизации пользователей*/
    private static final AuthorizationPanel authorizationPanel = new AuthorizationPanel();

    /** Статическое поле панели регистрации, предназначенное регистрации новых пользователей*/
    private static final RegistrationPanel registrationPanel = new RegistrationPanel();

    public HomePanel() {
    }

    /**
     * Метод для вывода панели регистрации и авторизации,
     * вызывает метод {@link HomePanel#startPageChooseAction()}
     */
    public void printStartPage() {
        System.out.println("""
                Здравствуйте, вас приветствует приложение по управлению коворкинг-пространством\s
                 Выберите действие:\s
                 1 - Авторизация\s
                 2 - Регистрация\s
                 0 - Выход из приложения""");
        startPageChooseAction();
    }

    /**
     * Метод считывает ввод пользователя и на его основе вызывает методы <br>
     * 1 - {@link AuthorizationPanel#logOn()} Авторизация<br>
     * 2 - {@link RegistrationPanel#signUp()} Регистрация<br>
     * 0 - Завершение работы приложения<br>
     * В других случаях выводит информацию о некорректном вводе данных и рекурсивно вызывает метод
     */
    private void startPageChooseAction() {
        switch (ConsoleReader.PageChoose()) {
            case 1 ->
                authorizationPanel.logOn();
            case 2 ->
                registrationPanel.signUp();
            case 0 ->
                System.exit(0);
            default -> {
                System.out.println("Некорректный ввод данных, повторите попытку");
                startPageChooseAction();
            }
        }
    }
}
