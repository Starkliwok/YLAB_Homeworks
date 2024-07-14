package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.annotation.Auditable;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для взаимодействия с пользователями
 * @author Денис Попов
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**Поле ДАО слоя для взаимодействия с пользователями*/
    private final UserDAO userDAO;

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public User getUser(String username, String password) {
        return userDAO.getUser(username, password);
    }

    @Override
    public User getUser(long id) {
        return userDAO.getUser(id);
    }

    @Override
    public Long getUserId(String username) {
        return userDAO.getUserId(username);
    }

    @Auditable
    @Override
    public void saveUser(String username, String password) throws UserAlreadyExistsException {
        checkUserLogin(username);
        userDAO.saveUser(username, password);
    }

    @Override
    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    @Override
    public void deleteUser(long id) {
        userDAO.deleteUser(id);
    }

    @Override
    public boolean isUserExist(String username) {
        return userDAO.isUserExist(username);
    }

    @Override
    public void checkUserLogin(String username) throws UserAlreadyExistsException {
        if(isUserExist(username)) {
            throw new UserAlreadyExistsException("User with this login already exists");
        }
    }
}
