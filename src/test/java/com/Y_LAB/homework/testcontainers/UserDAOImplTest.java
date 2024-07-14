package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContainerConfig.class})
class UserDAOImplTest {

    @Autowired
    private UserDAO userDAO;

    private User user;

    @BeforeEach
    void init() {
        user = new User(54, "654969546549", "gfs54439gi39");
        userDAO.saveUser(user.getUsername(), user.getPassword());
        user = userDAO.getUser(user.getUsername(), user.getPassword());
    }

    @AfterEach
    void clear() {
        userDAO.deleteUser(user.getId());
    }

    @Test
    @DisplayName("Проверка на увеличение общего числа пользователей при добавлении новой записи в бд")
    public void getAllUsers() {
        String username = "example";
        String password = "example";
        int expected = userDAO.getAllUsers().size() + 1;
        userDAO.saveUser(username, password);

        int actual = userDAO.getAllUsers().size();

        assertThat(actual).isEqualTo(expected);
        userDAO.deleteUser(userDAO.getUser(username, password).getId());
    }

    @Test
    @DisplayName("Получение пользователя по логину и паролю из бд")
    public void getUserWithUsernameAndPassword() {
        User actual = userDAO.getUser(user.getUsername(), user.getPassword());

        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Получение пользователя по id из бд")
    public void getUserWithID() {
        User actual = userDAO.getUser(user.getId());

        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Получение администратора по id из бд")
    public void getAdminWithID() {
        User actual = userDAO.getUser(userDAO.getUser("root", "root").getId());

        assertThat(actual).isEqualTo(userDAO.getUser("root", "root"));
        assertThat(actual).isExactlyInstanceOf(Admin.class);
    }

    @Test
    @DisplayName("Сохранение пользователя и проверка на наличие новой записи в бд")
    public void saveUser() {
        String username = "some user";
        String password = "some password";
        int expected = userDAO.getAllUsers().size() + 1;

        userDAO.saveUser(username, password);
        int actual = userDAO.getAllUsers().size();
        User user = userDAO.getUser(username, password);

        assertThat(actual).isEqualTo(expected);
        assertThat(username).isEqualTo(user.getUsername());
        assertThat(password).isEqualTo(user.getPassword());
        userDAO.deleteUser(user.getId());
    }

    @Test
    @DisplayName("Обновление пользователя и проверка на обновление записи в бд")
    public void updateUser() {
        String newPassword = "root2";
        user.setPassword(newPassword);

        userDAO.updateUser(user);

        assertThat(user).isEqualTo(userDAO.getUser(user.getUsername(), newPassword));
    }

    @Test
    @DisplayName("Удаление пользователя и проверка на отсутствие записи в бд")
    public void deleteUser() {
        User mustBeNotNull = userDAO.getUser(user.getId());

        userDAO.deleteUser(user.getId());
        User mustBeNull = userDAO.getUser(user.getUsername(), user.getPassword());

        assertThat(mustBeNotNull).isNotNull();
        assertThat(mustBeNull).isNull();
    }

    @Test
    @DisplayName("Проверка существующих и выдуманных логинов на существование в бд")
    void isUserExist() {
        boolean realUser = userDAO.isUserExist(user.getUsername());
        boolean fakeUser = userDAO.isUserExist("43243242346546453");

        assertThat(realUser).isTrue();
        assertThat(fakeUser).isFalse();
    }
}