package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.entity.roles.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDAOImplTest {

    private UserDAOImpl userDAO;

    private User user;

    private String username;

    private String password;

    @BeforeEach
    void init() {
        userDAO = new UserDAOImpl();
        username = "user";
        password = "password";
        user = new User(username, password);
    }

    @AfterEach
    void clear() {
        userDAO.deleteUser(user);
    }

    @Test
    @DisplayName("Получение всех логинов")
    void getUsernameSet() {
        int expected = userDAO.getUsernameSet().size() + 1;
        userDAO.addUserToUserSet(user);

        int actual = userDAO.getUsernameSet().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение пользователя")
    void getUserFromUserSet() {
        userDAO.addUserToUserSet(user);

        User actual = userDAO.getUserFromUserSet(username, password);

        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Добавление пользователя")
    void addUserToUserSet() {
        userDAO.addUserToUserSet(user);
        User actual = userDAO.getUserFromUserSet(username, password);

        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Обновление логина пользователя")
    void updateUsername() {
        String newUsername = "newPassword";
        user.setUsername(newUsername);
        userDAO.updateUsername(user, newUsername);

        User actual = userDAO.getUserFromUserSet(username, password);
        User actual2 = userDAO.getUserFromUserSet(newUsername, password);

        assertThat(actual).isNull();
        assertThat(actual2).isEqualTo(user);
    }

    @Test
    @DisplayName("Обновление пароля пользователя")
    void updateUserPassword() {
        String newPassword = "newPassword";
        user.setPassword(newPassword);
        userDAO.updateUserPassword(user, newPassword);

        User actual2 = userDAO.getUserFromUserSet(username, newPassword);

        assertThat(actual2).isEqualTo(user);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        userDAO.addUserToUserSet(user);

        User actual = userDAO.getUserFromUserSet(username, password);
        userDAO.deleteUser(user);
        User actual2 = userDAO.getUserFromUserSet(username, password);

        assertThat(actual).isEqualTo(user);
        assertThat(actual2).isNull();
    }

    @Test
    @DisplayName("Получение всех логинов и паролей")
    void getAllReservedUsernamesAndPasswords() {
        int expected = userDAO.getAllReservedUsernamesAndPasswords().size() + 1;
        assertThat(userDAO.getAllReservedUsernamesAndPasswords().containsKey(username)).isFalse();
        userDAO.addUserToUserSet(user);

        int actual = userDAO.getAllReservedUsernamesAndPasswords().size();

        assertThat(userDAO.getAllReservedUsernamesAndPasswords().containsKey(username)).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getUserSet() {
        int expected = userDAO.getUserSet().size() + 1;
        assertThat(userDAO.getUserSet().contains(user)).isFalse();
        userDAO.addUserToUserSet(user);

        int actual = userDAO.getUserSet().size();

        assertThat(userDAO.getUserSet().contains(user)).isTrue();
        assertThat(actual).isEqualTo(expected);
    }
}