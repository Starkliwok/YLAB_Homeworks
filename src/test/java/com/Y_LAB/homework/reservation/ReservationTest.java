package com.Y_LAB.homework.reservation;

import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    private Reservation reservation;

    private final ReservationService reservationService = new ReservationServiceImpl();

    @BeforeEach
    @DisplayName("")
    void init() {
        reservation = new Reservation();
        reservationService.addReservation(reservation);
    }

    @Test
    @DisplayName("Получение уникального идентификатора брони")
    void getId() {
        int expected = reservationService.getReservation(reservation.getId()).getId();
        int actual = reservation.getId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение пользователя")
    void getUser() {
        User expected = new User("user2", "password2");
        reservation.setUser(expected);

        User actual = reservation.getUser();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение даты начала брони")
    void getStartDate() {
        LocalDateTime expected = LocalDateTime.now();
        reservation.setStartDate(expected);

        LocalDateTime actual = reservation.getStartDate();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение даты конца брони")
    void getEndDate() {
        LocalDateTime expected = LocalDateTime.now();
        reservation.setEndDate(expected);

        LocalDateTime actual = reservation.getEndDate();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение места для бронирования")
    void getReservationPlace() {
        ReservationPlace expected = new Workplace("006", 542.2, 65.3);
        reservation.setReservationPlace(expected);

        ReservationPlace actual = reservation.getReservationPlace();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение пользователя")
    void setUser() {
        User expected = new User("user20", "password20");

        reservation.setUser(expected);
        User actual = reservation.getUser();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение даты начала брони")
    void setStartDate() {
        LocalDateTime expected = LocalDateTime.now().plusDays(43);

        reservation.setStartDate(expected);
        LocalDateTime actual = reservation.getStartDate();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение даты конца брони")
    void setEndDate() {
        LocalDateTime expected = LocalDateTime.now().plusDays(42);

        reservation.setStartDate(expected);
        LocalDateTime actual = reservation.getStartDate();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение места для бронирования")
    void setReservationPlace() {
        ReservationPlace expected = new Workplace("432", 654, 3235.2);

        reservation.setReservationPlace(expected);
        ReservationPlace actual = reservation.getReservationPlace();

        assertThat(actual).isEqualTo(expected);
    }
}