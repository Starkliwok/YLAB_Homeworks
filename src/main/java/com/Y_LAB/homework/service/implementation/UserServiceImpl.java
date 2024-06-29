package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.exception.user.auth.RegistrationException;
import com.Y_LAB.homework.exception.user.auth.PasswordFormatException;
import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.user.auth.UsernameFormatException;
import com.Y_LAB.homework.service.UserService;
import lombok.AllArgsConstructor;

import java.util.List;

import static com.Y_LAB.homework.constants.UserConstants.*;
import static com.Y_LAB.homework.constants.UserConstants.PASSWORD_MAX_LENGTH;

/**
 * Сервис для взаимодействия с пользователями
 * @author Денис Попов
 * @version 2.0
 */
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    /**Поле ДАО слоя для взаимодействия с пользователями*/
    private final UserDAO userDAO;

    public UserServiceImpl() {
        userDAO = new UserDAOImpl();
    }

    /**{@inheritDoc}*/
    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**{@inheritDoc}*/
    @Override
    public User getUser(String username, String password) {
        return userDAO.getUser(username, password);
    }

    /**{@inheritDoc}*/
    @Override
    public User getUser(long id) {
        return userDAO.getUser(id);
    }

    /**{@inheritDoc}*/
    @Override
    public void saveUser(String username, String password) throws RegistrationException {
        try {
            checkUserLogin(username);
            checkUserPassword(password);
        } catch (RegistrationException e) {
            throw new RegistrationException(e.getMessage());
        }
        userDAO.saveUser(username, password);
    }

    /**{@inheritDoc}*/
    @Override
    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    /**{@inheritDoc}*/
    @Override
    public void deleteUser(long id) {
        userDAO.deleteUser(id);
    }

    /**{@inheritDoc}*/
    @Override
    public boolean isUserExist(String username) {
        return userDAO.isUserExist(username);
    }

    /**{@inheritDoc}*/
    @Override
    public void checkUserLogin(String username) throws UsernameFormatException, UserAlreadyExistsException {
        if(username.length() < LOGIN_MIN_LENGTH || username.length() > LOGIN_MAX_LENGTH) {
            throw new UsernameFormatException("Минимальная длина логина - " + LOGIN_MIN_LENGTH +
                    ", максимальная длина логина - " + LOGIN_MAX_LENGTH + " символов, повторите попытку\n");
        } else if(isUserExist(username)) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует, повторите попытку\n");
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void checkUserPassword(String password) throws PasswordFormatException {
        if(password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new PasswordFormatException("Минимальная длина пароля - " + PASSWORD_MIN_LENGTH +
                    ", максимальная длина пароля - " + PASSWORD_MAX_LENGTH + " символов, повторите попытку\n");
        }
    }
}
