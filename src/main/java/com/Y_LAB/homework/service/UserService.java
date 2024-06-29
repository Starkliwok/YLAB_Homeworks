package com.Y_LAB.homework.service;

import com.Y_LAB.homework.entity.roles.User;
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

    /**
     * Метод для сохранения пользователя
     * @param username логин пользователя
     * @param password пароль пользователя
     * @throws RegistrationException Если длина логина или пароля превышает, или не соответствует допустимой длине
     */
    void saveUser(String username, String password) throws RegistrationException;

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
     * @throws UsernameFormatException Если логин больше или меньше допустимой длины логина
     * @throws UserAlreadyExistsException Если пользователь с таким логином уже существует
     */
    void checkUserLogin(String username) throws UsernameFormatException, UserAlreadyExistsException;

    /**
     * Метод для проверки пароля пользователя
     * @param password пароль пользователя
     * @throws PasswordFormatException Если пароль больше или меньше допустимой длины пароля
     */
    void checkUserPassword(String password) throws PasswordFormatException;
}