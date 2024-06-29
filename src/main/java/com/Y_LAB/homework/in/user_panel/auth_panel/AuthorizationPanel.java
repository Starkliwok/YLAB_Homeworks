package com.Y_LAB.homework.in.user_panel.auth_panel;

import com.Y_LAB.homework.entity.roles.Admin;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.exception.user.auth.WrongUsernameAndPasswordException;
import com.Y_LAB.homework.in.user_panel.AdminPanel;
import com.Y_LAB.homework.in.user_panel.HomePanel;
import com.Y_LAB.homework.in.user_panel.UserPanel;
import com.Y_LAB.homework.in.util.ConsoleReader;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;

/**
 * Класс для вывода панели для авторизации пользователя и взаимодействия с ней
 * @author Денис Попов
 * @version 2.0
 */
public class AuthorizationPanel {

    /** Поле сервиса пользователей, предназначенное взаимодействия с пользователями*/
    private final UserService userService;

    /** Статическое поле панели администратора, предназначенное для взаимодействия с выбором администратора*/
    private static final AdminPanel adminPanel = new AdminPanel();

    /** Статическое поле домашней панели, предназначенное для вывода информации новым пользователям*/
    private static final HomePanel homePanel = new HomePanel();

    /** Статическое поле панели пользователя, предназначенное для взаимодействия с выбором пользователя*/
    private static final UserPanel userPanel = new UserPanel();

    public AuthorizationPanel() {
        userService = new UserServiceImpl();
    }

    /**
     * Метод для вывода информации по авторизации пользователя,
     * считывает введённые данные пользователя с помощью метода <br>
     * {@link ConsoleReader#readStringValue()},
     * в зависимости от типа полученного пользователя из метода <br> {@link UserService#getUser(String, String)}
     * вызывает соответствующий класс для пользователя - <br>
     * {@link AdminPanel#printAdminPage(Admin)}<br> или <br>{@link UserPanel#printUserPage(User)}<br>
     * для вывода информации по возможным действиям пользователя,
     * в случае когда пользователь по указанным логину и паролю не существует - вызывается домашняя страница
     * {@link HomePanel#printStartPage()}
     */
    public void logOn() {
        System.out.print("Введите логин: ");
        String username = ConsoleReader.readStringValue();
        System.out.print("Введите пароль: ");
        String password = ConsoleReader.readStringValue();
        User user;
        try {
            user = userService.getUser(username, password);
            if(user == null) {
                throw new WrongUsernameAndPasswordException("Неверный логин или пароль, повторите попытку");
            }
            System.out.println("Вы успешно авторизовались как " + user.getUsername() + "\n");
            if(user instanceof Admin){
                adminPanel.printAdminPage((Admin) user);
            } else {
                userPanel.printUserPage(user);
            }
        } catch (WrongUsernameAndPasswordException e) {
            System.out.println(e.getMessage());
            homePanel.printStartPage();
        }
    }
}
