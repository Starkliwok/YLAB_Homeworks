package com.Y_LAB.homework.reservation;

import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void init() {
        Workplace workplace = new Workplace(1, "name", 2.1, 2, 3);
        reservation = new Reservation(1, 1, LocalDateTime.now(), LocalDateTime.now(), workplace);
    }

    @Test
    @DisplayName("Получение уникального идентификатора брони")
    void getId() {
        long expected = 1;
        long actual = reservation.getId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение уникального идентификатора пользователя")
    void getUser() {
        long expected = 3;
        reservation.setUserId(expected);

        long actual = reservation.getUserId();

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
        ReservationPlace expected = new Workplace(1, "w2", 3.2, 542.2, 65);
        reservation.setReservationPlace(expected);

        ReservationPlace actual = reservation.getReservationPlace();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение уникального идентификатора пользователя")
    void setUserId() {
        long expected = 65;

        reservation.setUserId(expected);
        long actual = reservation.getUserId();

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
        ReservationPlace expected = new Workplace(54, "w3", 4.2, 51.2, 35);

        reservation.setReservationPlace(expected);
        ReservationPlace actual = reservation.getReservationPlace();

        assertThat(actual).isEqualTo(expected);
    }
}