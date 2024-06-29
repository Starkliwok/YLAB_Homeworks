package com.Y_LAB.homework.testcontainers.reservation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import com.Y_LAB.homework.util.reservation.FreeReservationSlot;
import com.Y_LAB.homework.util.reservation.ReservationDateTimeGenerator;
import finder.ObjectFinderForTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class FreeReservationSlotTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withExposedPorts(5433, 5432)
                    .withUsername("postgres")
                    .withPassword("starkliw")
                    .withDatabaseName("postgres");

    private static Connection connection;

    private static ReservationDAO reservationDAO;

    private static ReservationPlaceDAO reservationPlaceDAO;

    private static FreeReservationSlot freeReservationSlot;

    private ReservationPlace reservationPlace;

    private Reservation reservation;

    private Reservation reservation2;

    @BeforeAll
    static void beforeAll() {
        Properties properties = new Properties();
        properties.setProperty(PROPERTIES_URL_KEY, postgresContainer.getJdbcUrl());
        properties.setProperty(PROPERTIES_USERNAME_KEY, postgresContainer.getUsername());
        properties.setProperty(PROPERTIES_PASSWORD_KEY, postgresContainer.getPassword());
        LiquibaseMigration.initMigration(ConnectionToDatabase.getConnectionFromProperties(properties));
        connection = ConnectionToDatabase.getConnectionFromProperties(properties);

        reservationPlaceDAO = new ReservationPlaceDAOImpl(connection);
        reservationDAO = new ReservationDAOImpl(connection, reservationPlaceDAO);
        freeReservationSlot = new FreeReservationSlot(
                new ReservationPlaceServiceImpl(reservationPlaceDAO),
                new ReservationServiceImpl(reservationDAO));
    }

    @BeforeEach
    void init() {
        reservationPlace = new Workplace("5435", 7657, 23, 11);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, connection);
        reservation = new Reservation(1, 4,
                LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(22).withMinute(0).withSecond(0).withNano(0), reservationPlace);
        reservation2 = new Reservation(1, 4,
                LocalDateTime.now().plusDays(1).withMinute(0).withHour(8).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(1).withMinute(0).withHour(20).withSecond(0).withNano(0), reservationPlace);
        reservationDAO.saveReservation(reservation);
        reservationDAO.saveReservation(reservation2);
        ObjectFinderForTests.setReservationIdFromDB(reservation, connection);
        ObjectFinderForTests.setReservationIdFromDB(reservation2, connection);
    }

    @Test
    @DisplayName("Метод должен вернуть все доступные даты для бронирования места")
    void getAllAvailableReservations() {
        int expected = freeReservationSlot.getAllAvailablePlaceForReservations().size() + 1;
        ReservationPlace reservationPlace = new Workplace("123453", 876, 32, 11);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);

        int actual = freeReservationSlot.getAllAvailablePlaceForReservations().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть все доступные даты для бронирования места с удалением одного элемента")
    void getAllAvailableDatesForReservePlace() {
        List<LocalDate> expected = ReservationDateTimeGenerator.generateDates();
        expected.remove(reservation.getStartDate().toLocalDate());

        List<LocalDate> actual = freeReservationSlot.getAllAvailableDatesForReservePlace(reservationPlace);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть доступные времена в промежутке 20-21, 21-22 для бронирования места")
    void shouldReturnMapWith2AvailableTimes() {
        Map<LocalTime, LocalTime> expected = new LinkedHashMap<>();
        expected.put(LocalTime.of(20, 0), LocalTime.of(21, 0));
        expected.put(LocalTime.of(21, 0), LocalTime.of(22, 0));

        Map<LocalTime, LocalTime> actual = freeReservationSlot.getAllAvailableTimesForReservePlace(
                reservationPlace, reservation2.getStartDate().toLocalDate());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть доступные времена без удалений доступных промежутков для бронирования места")
    void shouldReturnMapWithoutDeleteElements() {
        Map<LocalTime, LocalTime> expected = ReservationDateTimeGenerator.generateTimesPeriod();

        Map<LocalTime, LocalTime> actual = freeReservationSlot.getAllAvailableTimesForReservePlace(
                reservationPlace, reservation2.getStartDate().toLocalDate().plusDays(3));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть пустую коллекцию для бронирования места")
    void shouldReturnEmptyMap() {
        Map<LocalTime, LocalTime> actual = freeReservationSlot.getAllAvailableTimesForReservePlace(
                reservationPlace, reservation.getStartDate().toLocalDate());

        assertThat(actual.isEmpty()).isTrue();
    }
}