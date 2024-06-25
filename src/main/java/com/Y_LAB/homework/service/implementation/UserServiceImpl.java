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

    /** {@inheritDoc}*/
    @Override
    public Set<User> getUserSet() {
        return userDAO.getUserSet();
    }

    /** {@inheritDoc}*/
    @Override
    public Set<String> getUsernameSet() {
        return userDAO.getUsernameSet();
    }

    /** {@inheritDoc}*/
    @Override
    public Map<String, String> getAllReservedUsernamesAndPasswords() {
        return userDAO.getAllReservedUsernamesAndPasswords();
    }

    /** {@inheritDoc}*/
    @Override
    public User getUserFromUserSet(String username, String password) {
        return userDAO.getUserFromUserSet(username, password);
    }

    /** {@inheritDoc}*/
    @Override
    public void addUserToUserSet(User user) {
        userDAO.addUserToUserSet(user);
    }

    /** {@inheritDoc}*/
    @Override
    public void updateUsername(User user, String newUsername) {
        userDAO.updateUsername(user, newUsername);
    }

    /** {@inheritDoc}*/
    @Override
    public void updateUserPassword(User user, String newPassword) {
        userDAO.updateUserPassword(user, newPassword);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }
}
