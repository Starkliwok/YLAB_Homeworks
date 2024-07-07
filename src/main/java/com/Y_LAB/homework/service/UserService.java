package com.Y_LAB.homework.service;

import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.exception.user.auth.*;

import java.util.List;

/**
 * Интерфейс описывает сервис для взаимодействия с пользователями
 * @author Денис Попов
 * @version 2.0
 */
public interface UserService {

    /**
     * Метод для получения всех пользователей
     * @return Коллекция всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Метод для получения пользователя
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return пользователь
     */
    User getUser(String username, String password);

    /**
     * Метод для получения пользователя
     * @param id уникальный идентификатор пользователя
     * @return пользователь
     */
    User getUser(long id);

    Long getUserId(String username);

    /**
     * Метод для сохранения пользователя
     * @param username логин пользователя
     * @param password пароль пользователя
     * @throws UserAlreadyExistsException Если пользователь с таким логином уже существует
     */
    void saveUser(String username, String password) throws UserAlreadyExistsException;

    /**
     * Метод для обновления пользователя
     * @param user пользователь
     */
    void updateUser(User user);

    /**
     * Метод для удаления пользователя
     * @param id идентификационный номер пользователя
     */
    void deleteUser(long id);

    /**
     * Метод для проверки существования пользователя по логину
     * @param username логин пользователя
     * @return True - пользователь с таким логином существует. False - пользователя с таким логином не существует
     */
    boolean isUserExist(String username);

    /**
     * Метод для проверки логина пользователя
     * @param username логин пользователя
     * @throws UserAlreadyExistsException Если пользователь с таким логином уже существует
     */
    void checkUserLogin(String username) throws UserAlreadyExistsException;
}
