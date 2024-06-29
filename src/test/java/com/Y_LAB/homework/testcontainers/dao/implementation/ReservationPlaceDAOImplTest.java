package com.Y_LAB.homework.testcontainers.dao.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.entity.reservation.ConferenceRoom;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import finder.ObjectFinderForTests;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ReservationPlaceDAOImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withExposedPorts(5433, 5432)
                    .withUsername("postgres")
                    .withPassword("starkliw")
                    .withDatabaseName("postgres");
    private static Connection connection;
    private static ReservationPlaceDAO reservationPlaceDAO;
    private static ReservationPlace reservationPlace;
    private static ReservationPlace reservationPlace1;
    private static ReservationPlace reservationPlace2;

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
    }

    @BeforeEach
    void init() {
        reservationPlace = new Workplace("009", 543, 34, 20);
        reservationPlace1 = new Workplace(3266,"006", 12.2, 57, 22);
        reservationPlace2 = new ConferenceRoom(176,"7", 43.2, 87, 34);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        reservationPlaceDAO.saveReservationPlace(reservationPlace1);
        reservationPlaceDAO.saveReservationPlace(reservationPlace2);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, connection);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace1, connection);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace2, connection);
    }

    @AfterEach
    void clear() {
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace1.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace2.getId());
    }

    @Test
    @DisplayName("Получение всех мест для бронирования из бд")
    void getAllReservationPlaces() {
        int expected = reservationPlaceDAO.getAllReservationPlaces().size() + 1;
        reservationPlaceDAO.saveReservationPlace(reservationPlace);

        int actual = reservationPlaceDAO.getAllReservationPlaces().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех мест для бронирования по типу из бд")
    void getAllReservationPlacesByTypes() {
        List<ReservationPlace> workplaces = reservationPlaceDAO.getAllReservationPlacesByTypes(new Workplace());
        List<ReservationPlace> conferenceRooms = reservationPlaceDAO.getAllReservationPlacesByTypes(new ConferenceRoom());

        for(ReservationPlace actual : workplaces) {
            assertThat(actual).isExactlyInstanceOf(Workplace.class);
        }
        for(ReservationPlace actual : conferenceRooms) {
            assertThat(actual).isExactlyInstanceOf(ConferenceRoom.class);
        }
    }

    @Test
    @DisplayName("Получение места для бронирования из бд")
    void getReservationPlace() {
        ReservationPlace actual = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(reservationPlace);
    }

    @Test
    @DisplayName("Добавление места для бронирования в бд")
    void saveReservationPlace() {
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        ReservationPlace actual = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, connection);
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual).isNull();
        assertThat(actual2).isEqualTo(reservationPlace);
    }

    @Test
    @DisplayName("Обновление места для бронирования в бд")
    void updateReservationPlace() {
        String newName = "002";

        String actual1 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId()).getName();
        reservationPlace.setName(newName);
        reservationPlaceDAO.updateReservationPlace(reservationPlace);
        String actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId()).getName();

        assertThat(actual1).isNotEqualTo(newName);
        assertThat(actual2).isEqualTo(newName);
    }

    @Test
    @DisplayName("Удаление места для бронирования из бд")
    void deleteReservationPlace() {
        ReservationPlace actual = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual).isEqualTo(reservationPlace);
        assertThat(actual2).isNull();
    }
}