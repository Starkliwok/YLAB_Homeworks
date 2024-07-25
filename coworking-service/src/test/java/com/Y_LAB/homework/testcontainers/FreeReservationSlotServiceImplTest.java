package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.finder.ObjectFinderForTests;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.reservation.ReservationDateTimeGenerator;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestContainerConfig.class})
class FreeReservationSlotServiceImplTest {

    @Autowired
    private Connection connection;

    @Autowired
    private ObjectFinderForTests objectFinderForTests;

    @Autowired
    private ReservationDAO reservationDAO;

    @Autowired
    private ReservationPlaceDAO reservationPlaceDAO;

    @Autowired
    private FreeReservationSlotService freeReservationSlot;

    private ReservationPlace reservationPlace;

    private Reservation reservation;

    private Reservation reservation2;

    @BeforeEach
    void init() {
        reservationPlace = new Workplace("5435", 7657, 23, 11);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        objectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, connection);
        reservation = new Reservation(1, 4,
                LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(22).withMinute(0).withSecond(0).withNano(0), reservationPlace);
        reservation2 = new Reservation(1, 4,
                LocalDateTime.now().plusDays(1).withMinute(0).withHour(8).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(1).withMinute(0).withHour(20).withSecond(0).withNano(0), reservationPlace);
        reservationDAO.saveReservation(reservation);
        reservationDAO.saveReservation(reservation2);
        objectFinderForTests.setReservationIdFromDB(reservation, connection);
        objectFinderForTests.setReservationIdFromDB(reservation2, connection);
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

        Map<LocalTime, LocalTime> actual = freeReservationSlot.getAllAvailableTimesForReservation(
                reservationPlace, reservation2.getStartDate().toLocalDate());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть доступные времена без удалений доступных промежутков для бронирования места")
    void shouldReturnMapWithoutDeleteElements() {
        Map<LocalTime, LocalTime> expected = ReservationDateTimeGenerator.generateTimesPeriod();

        Map<LocalTime, LocalTime> actual = freeReservationSlot.getAllAvailableTimesForReservation(
                reservationPlace, reservation2.getStartDate().toLocalDate().plusDays(3));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть пустую коллекцию для бронирования места")
    void shouldReturnEmptyMap() {
        Map<LocalTime, LocalTime> actual = freeReservationSlot.getAllAvailableTimesForReservation(
                reservationPlace, reservation.getStartDate().toLocalDate());

        assertThat(actual.isEmpty()).isTrue();
    }
}