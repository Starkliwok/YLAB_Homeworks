package com.Y_LAB.homework.reservation;

import com.Y_LAB.homework.entity.reservation.ConferenceRoom;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationPlaceTest {

    private ReservationPlace reservationPlace;

    private final ReservationPlaceService reservationPlaceService = new ReservationPlaceServiceImpl();

    @BeforeEach
    void init() {
        reservationPlace = new ConferenceRoom("001", 150.2, 120.2, 14);
        reservationPlaceService.addReservationPlace(reservationPlace);
    }

    @Test
    @DisplayName("Получение уникального идентификатора места")
    void getId() {
        int expected = reservationPlaceService.getReservationPlace(reservationPlace.getId()).getId();
        int actual = reservationPlace.getId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение названия места")
    void getName() {
        String expected = "002";
        reservationPlace.setName("002");

        String actual = reservationPlace.getName();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение площади места")
    void getPlaceArea() {
        double expected = 200;
        reservationPlace.setPlaceArea(expected);

        double actual = reservationPlace.getPlaceArea();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение стоимости аренды за день в долларах")
    void getCostPerDayInDollars() {
        double expected = 300;
        reservationPlace.setCostPerDayInDollars(expected);

        double actual = reservationPlace.getCostPerDayInDollars();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение количества сидений")
    void getNumberOfSeats() {
        int expected = 300;
        reservationPlace.setNumberOfSeats(expected);

        int actual = reservationPlace.getNumberOfSeats();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение названия места")
    void setName() {
        String expected = reservationPlace.getName() + "2";

        reservationPlace.setName(expected);
        String actual = reservationPlace.getName();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение площади места")
    void setPlaceArea() {
        double expected = reservationPlace.getPlaceArea() + 4.3;

        reservationPlace.setPlaceArea(expected);
        double actual = reservationPlace.getPlaceArea();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение стоимости аренды в долларах")
    void setCostPerDayInDollars() {
        double expected = reservationPlace.getCostPerDayInDollars() + 4.6;

        reservationPlace.setCostPerDayInDollars(expected);
        double actual = reservationPlace.getCostPerDayInDollars();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение количества сидений")
    void setNumberOfSeats() {
        int expected = reservationPlace.getNumberOfSeats() + 3;

        reservationPlace.setNumberOfSeats(expected);
        int actual = reservationPlace.getNumberOfSeats();

        assertThat(actual).isEqualTo(expected);
    }
}