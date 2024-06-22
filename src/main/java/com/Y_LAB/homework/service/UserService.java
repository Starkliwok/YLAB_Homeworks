package com.Y_LAB.homework.service;

import com.Y_LAB.homework.entity.roles.User;

import java.util.Map;
import java.util.Set;

/**
 * Интерфейс описывает сервис для взаимодействия с пользователями
 * @author Денис Попов
 * @version 1.0
 */
public interface UserService {

    /**
     * Метод для получения коллекции всех пользователей
     * @return все пользователи в виде множества
     */
    Set<User> getUserSet();

    /**
     * Метод для получения коллекции всех логинов
     * @return все логины в виде множества
     */
    Set<String> getUsernameSet();

    /**
     * Метод для получения коллекции всех логинов и их паролей
     * @return все логины и пароли пользователей в виде ключа = значение
     */
    Map<String, String> getAllReservedUsernamesAndPasswords();

    /**
     * Метод для получения пользователя
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return возвращает пользователя,
     */
    User getUserFromUserSet(String username, String password);

    /**
     * Метод для добавления пользователя
     * @param user пользователь
     */
    void addUserToUserSet(User user);

    /**
     * Метод для обновления логина пользователя
     * @param user пользователь
     * @param newUsername новый логин
     */
    void updateUsername(User user, String newUsername);

    /**
     * Метод для обновления пароля пользователя
     * @param user пользователь
     * @param newPassword новый пароль
     */
    void updateUserPassword(User user, String newPassword);

    /**
     * Метод для удаления пользователя
     * @param user пользователь
     */
    void deleteUser(User user);
}
