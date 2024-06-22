package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.service.UserService;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * Сервис для взаимодействия с пользователями
 * @author Денис Попов
 * @version 1.0
 */
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    /**Поле ДАО слоя для взаимодействия с пользователями*/
    private final UserDAO userDAO;

    public UserServiceImpl() {
        userDAO = new UserDAOImpl();
    }

    /**
     * Метод вызывает {@link UserDAO#getUsernameSet} для получения коллекции всех пользователей
     * @return все логины в виде множества
     */
    @Override
    public Set<User> getUserSet() {
        return userDAO.getUserSet();
    }

    /**
     * Метод {@link UserDAO#getUsernameSet} вызывает для получения коллекции всех логинов
     * @return все логины в виде множества
     */
    @Override
    public Set<String> getUsernameSet() {
        return userDAO.getUsernameSet();
    }

    /**
     * Метод вызывает {@link UserDAO#getAllReservedUsernamesAndPasswords} для получения коллекции всех логинов и паролей
     * в виде ключа=значение
     * @return все логины и пароли в виде ключа=значение
     */
    @Override
    public Map<String, String> getAllReservedUsernamesAndPasswords() {
        return userDAO.getAllReservedUsernamesAndPasswords();
    }

    /**
     * Метод вызывает {@link UserDAO#getUserFromUserSet} для получения пользователя
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return возвращает пользователя,
     */
    @Override
    public User getUserFromUserSet(String username, String password) {
        return userDAO.getUserFromUserSet(username, password);
    }

    /** Метод вызывает {@link UserDAO#addUserToUserSet} для сохранения пользователя
     * @param user пользователь
     */
    @Override
    public void addUserToUserSet(User user) {
        userDAO.addUserToUserSet(user);
    }

    /**
     * Метод вызывает {@link UserDAO#updateUsername} для изменения логина пользователя
     * @param user пользователь
     * @param newUsername новое имя
     */
    @Override
    public void updateUsername(User user, String newUsername) {
        userDAO.updateUsername(user, newUsername);
    }

    /**
     * Метод вызывает {@link UserDAO#updateUserPassword} для изменения пароля пользователя
     * @param user пользователь
     * @param newPassword новый пароль
     */
    @Override
    public void updateUserPassword(User user, String newPassword) {
        userDAO.updateUserPassword(user, newPassword);
    }

    /**
     * Метод вызывает {@link UserDAO#deleteUser} для удаления пользователя
     * @param user пользователь
     */
    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }
}
