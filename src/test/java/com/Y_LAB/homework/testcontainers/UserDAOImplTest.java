package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class UserDAOImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = TestcontrainerManager.getPostgreSQLContainer();

    private static UserDAO userDAO;

    private User user;

    @BeforeAll
    static void beforeAll() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(PROPERTIES_URL_KEY, postgresContainer.getJdbcUrl());
        properties.setProperty(PROPERTIES_USERNAME_KEY, postgresContainer.getUsername());
        properties.setProperty(PROPERTIES_PASSWORD_KEY, postgresContainer.getPassword());
        LiquibaseMigration.initMigration(ConnectionToDatabase.getConnectionFromProperties(properties));
        Connection connection = ConnectionToDatabase.getConnectionFromProperties(properties);
        connection.setAutoCommit(false);

        userDAO = new UserDAOImpl(connection);
    }


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