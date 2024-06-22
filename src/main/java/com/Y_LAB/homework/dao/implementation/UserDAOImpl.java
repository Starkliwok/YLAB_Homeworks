package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.entity.roles.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Класс ДАО слоя для взаимодействия с пользователями
 * @author Денис Попов
 * @version 1.0
 */
public class UserDAOImpl implements UserDAO {

    /** Статическое поле которое содержит в себе логин в виде ключа и пароль в виде значения всех пользователей.*/
    private static final Map<String, String> allReservedUsernamesAndPasswords = new HashMap<>();

    /** Статическое поле которое содержит в себе всех пользователей.*/
    private static final Set<User> userSet = new HashSet<>();

    public UserDAOImpl() {
    }

    /**
     * Метод для получения коллекции всех пользователей из {@link UserDAOImpl#userSet}
     * @return все пользователи в виде множества
     */
    @Override
    public Set<User> getUserSet() {
        return userSet;
    }

    /**
     * Метод для получения коллекции всех логинов из {@link UserDAOImpl#allReservedUsernamesAndPasswords}
     * @return все логины в виде множества
     */
    @Override
    public Set<String> getUsernameSet() {
        return allReservedUsernamesAndPasswords.keySet();
    }

    /**
     * Метод для получения коллекции всех логинов и их паролей из {@link UserDAOImpl#allReservedUsernamesAndPasswords}
     * @return все логины и пароли пользователей в виде ключа = значение
     */
    @Override
    public Map<String, String> getAllReservedUsernamesAndPasswords() {
        return allReservedUsernamesAndPasswords;
    }

    /**
     * Метод для получения пользователя из {@link UserDAOImpl#userSet}
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return возвращает пользователя,
     */
    @Override
    public User getUserFromUserSet(String username, String password) {
        for (User element : userSet) {
            if (username.equals(element.getUsername()) && password.equals(element.getPassword())) {
                return element;
            }
        }
        return null;
    }

    /**
     * Метод для добавления пользователя в {@link UserDAOImpl#allReservedUsernamesAndPasswords} и
     * {@link UserDAOImpl#userSet}
     * @param user пользователь
     */
    @Override
    public void addUserToUserSet(User user) {
        userSet.add(user);
        allReservedUsernamesAndPasswords.put(user.getUsername(), user.getPassword());
    }

    /**
     * Метод для обновления логина пользователя в {@link UserDAOImpl#allReservedUsernamesAndPasswords} и
     * {@link UserDAOImpl#userSet}
     * @param user пользователь
     * @param newUsername новый логин
     */
    @Override
    public void updateUsername(User user, String newUsername) {
        allReservedUsernamesAndPasswords.remove(user.getUsername());
        user.setUsername(newUsername);
        allReservedUsernamesAndPasswords.put(newUsername, user.getPassword());
        userSet.add(user);
    }

    /**
     * Метод для обновления пароля пользователя в {@link UserDAOImpl#allReservedUsernamesAndPasswords} и
     * {@link UserDAOImpl#userSet}
     * @param user пользователь
     * @param newPassword новый пароль
     */
    @Override
    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        allReservedUsernamesAndPasswords.put(user.getUsername(), newPassword);
        userSet.add(user);
    }

    /**
     * Метод для удаления пользователя в {@link UserDAOImpl#allReservedUsernamesAndPasswords} и
     * {@link UserDAOImpl#userSet}
     * @param user пользователь
     */
    @Override
    public void deleteUser(User user) {
        userSet.remove(user);
        allReservedUsernamesAndPasswords.remove(user.getUsername());
    }
}
