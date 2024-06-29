package com.Y_LAB.homework.testcontainers.dao.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.entity.roles.User;
import finder.ObjectFinderForTests;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ReservationDAOImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withExposedPorts(5433, 5432)
                    .withUsername("postgres")
                    .withPassword("starkliw")
                    .withDatabaseName("postgres");
    private static Connection connection;

    private static ReservationDAO reservationDAO;

    private static UserDAO userDAO;

    private static ReservationPlaceDAO reservationPlaceDAO;

    private Reservation reservation;

    private ReservationPlace reservationPlace;

    private User user;

    @BeforeAll
    static void beforeAll() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(PROPERTIES_URL_KEY, postgresContainer.getJdbcUrl());
        properties.setProperty(PROPERTIES_USERNAME_KEY, postgresContainer.getUsername());
        properties.setProperty(PROPERTIES_PASSWORD_KEY, postgresContainer.getPassword());
        LiquibaseMigration.initMigration(ConnectionToDatabase.getConnectionFromProperties(properties));
        connection = ConnectionToDatabase.getConnectionFromProperties(properties);
        connection.setAutoCommit(false);

        reservationPlaceDAO = new ReservationPlaceDAOImpl(connection);
        reservationDAO = new ReservationDAOImpl(connection, reservationPlaceDAO);
        userDAO = new UserDAOImpl(connection);
    }

    @BeforeEach
    void init() {
        String username = "useruser654654";
        String password = "passwordpassword";
        userDAO.saveUser(username, password);
        user = userDAO.getUser(username, password);
        reservationPlace = new Workplace("009", 543, 34, 20);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, connection);
        reservation = new Reservation(1, user.getId(),
                LocalDateTime.now().withSecond(0).withNano(0),
                LocalDateTime.now().withSecond(0).withNano(0).plusHours(2), reservationPlace);
        reservationDAO.saveReservation(reservation);
        ObjectFinderForTests.setReservationIdFromDB(reservation, connection);
    }

    @AfterEach
    void clear() {
        reservationDAO.deleteReservation(reservation.getId());
        userDAO.deleteUser(user.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
    }

    @Test
    @DisplayName("Получение всех броней из бд")
    void getAllReservations() {
        reservationDAO.deleteReservation(reservation.getId());
        int expected = reservationDAO.getAllReservations().size() + 1;
        reservationDAO.saveReservation(reservation);
        ObjectFinderForTests.setReservationIdFromDB(reservation, connection);

        int actual = reservationDAO.getAllReservations().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней отсортированных по пользователям из бд")
    void getAllReservationsByUsers() {
        List<Reservation> expected = reservationDAO.getAllReservations().stream()
                .sorted(Comparator.comparing(Reservation::getUserId)).toList();

        List<Reservation> actual = reservationDAO.getAllReservationsByUsers();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected.isEmpty()).isFalse();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней отсортированных по датам из бд")
    void getAllReservationsByDate() {
        List<Reservation> expected = reservationDAO.getAllReservations().stream()
                .sorted(Comparator.comparing(Reservation::getStartDate)).collect(Collectors.toList());

        List<Reservation> actual = reservationDAO.getAllReservationsByDate();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected.isEmpty()).isFalse();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней пользователя из бд")
    void getAllUserReservations() {
        List<Reservation> expected = reservationDAO.getAllReservations().stream()
                .filter(x -> x.getUserId() == reservation.getUserId()).toList();

        List<Reservation> actual = reservationDAO.getAllUserReservations(reservation.getUserId());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение брони из бд")
    void getReservation() {
        Reservation actual = reservationDAO.getReservation(reservation.getId());

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(reservation);
    }

    @Test
    @DisplayName("Добавление брони в бд")
    void addReservation() {
        reservationDAO.deleteReservation(reservation.getId());
        Reservation actual = reservationDAO.getReservation(reservation.getId());
        reservationDAO.saveReservation(reservation);
        ObjectFinderForTests.setReservationIdFromDB(reservation, connection);
        Reservation actual2 = reservationDAO.getReservation(reservation.getId());

        assertThat(actual).isNull();
        assertThat(actual2).isEqualTo(reservation);
    }

    @Test
    @DisplayName("Обновление брони в бд")
    void updateReservation() {
        LocalDateTime newEndDate = reservation.getEndDate().plusHours(2);

        LocalDateTime actual1 = reservationDAO.getReservation(reservation.getId()).getEndDate();
        reservation.setEndDate(newEndDate);
        reservationDAO.updateReservation(reservation);
        LocalDateTime actual2 = reservationDAO.getReservation(reservation.getId()).getEndDate();

        assertThat(actual1).isNotEqualTo(newEndDate);
        assertThat(actual2).isEqualTo(newEndDate);
    }

    @Test
    @DisplayName("Удаление брони из бд")
    void deleteReservation() {
        Reservation actual = reservationDAO.getReservation(reservation.getId());
        reservationDAO.deleteReservation(reservation.getId());
        Reservation actual2 = reservationDAO.getReservation(reservation.getId());

        assertThat(actual).isEqualTo(reservation);
        assertThat(actual2).isNull();
    }
}