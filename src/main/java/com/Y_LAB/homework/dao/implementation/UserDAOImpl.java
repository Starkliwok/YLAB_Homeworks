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

    /** {@inheritDoc}*/
    @Override
    public Set<User> getUserSet() {
        return userSet;
    }

    /** {@inheritDoc}*/
    @Override
    public Set<String> getUsernameSet() {
        return allReservedUsernamesAndPasswords.keySet();
    }

    /** {@inheritDoc}*/
    @Override
    public Map<String, String> getAllReservedUsernamesAndPasswords() {
        return allReservedUsernamesAndPasswords;
    }

    /** {@inheritDoc}*/
    @Override
    public User getUserFromUserSet(String username, String password) {
        for (User element : userSet) {
            if (username.equals(element.getUsername()) && password.equals(element.getPassword())) {
                return element;
            }
        }
        return null;
    }

    /** {@inheritDoc}*/
    @Override
    public void addUserToUserSet(User user) {
        userSet.add(user);
        allReservedUsernamesAndPasswords.put(user.getUsername(), user.getPassword());
    }

    /** {@inheritDoc}*/
    @Override
    public void updateUsername(User user, String newUsername) {
        allReservedUsernamesAndPasswords.remove(user.getUsername());
        user.setUsername(newUsername);
        allReservedUsernamesAndPasswords.put(newUsername, user.getPassword());
        userSet.add(user);
    }

    /** {@inheritDoc}*/
    @Override
    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        allReservedUsernamesAndPasswords.put(user.getUsername(), newPassword);
        userSet.add(user);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteUser(User user) {
        userSet.remove(user);
        allReservedUsernamesAndPasswords.remove(user.getUsername());
    }
}
