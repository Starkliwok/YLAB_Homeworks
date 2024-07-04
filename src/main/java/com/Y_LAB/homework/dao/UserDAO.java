package com.Y_LAB.homework.dao;

import com.Y_LAB.homework.model.roles.User;

import java.util.List;

/**
 * Интерфейс описывает ДАО слой для взаимодействия с пользователями в базе данных
 * @author Денис Попов
 * @version 2.0
 */
public interface UserDAO {

    /**
     * Метод для получения коллекции всех пользователей из базы данных
     * @return все пользователи в виде множества
     */
    List<User> getAllUsers();

    /**
     * Метод для получения пользователя из базы данных
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return возвращает пользователя,
     */
    User getUser(String username, String password);

    /**
     * Метод для получения пользователя из базы данных
     * @param id уникальный идентификатор пользователя
     * @return пользователь
     */
    User getUser(long id);

    /**
     * Метод для сохранения пользователя в базу данных
     * @param username логин пользователя
     * @param password пароль пользователя
     */
    void saveUser(String username, String password);

    /**
     * Метод для обновления пользователя в базе данных
     * @param user пользователь
     */
    void updateUser(User user);

    /**
     * Метод для удаления пользователя из базы данных
     * @param id уникальный номер пользователя
     */
    void deleteUser(long id);

    /**
     * Метод для проверки существования пользователя по логину в базе данных
     * @param username логин пользователя
     * @return True - пользователь с таким логином существует. False - пользователя с таким логином не существует
     */
    boolean isUserExist(String username);
}
