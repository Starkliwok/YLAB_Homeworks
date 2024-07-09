package com.Y_LAB.homework.reservation;

import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationPlaceTest {

    private ReservationPlace reservationPlace;

    @BeforeEach
    void init() {
        reservationPlace = new ConferenceRoom(1,"001", 150.2, 120.2, 14);
    }

    @Test
    @DisplayName("Получение уникального идентификатора места")
    void getId() {
        int actual = reservationPlace.getId();

        assertThat(actual).isEqualTo(1);
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
        reservationPlace.setCostPerHour(expected);

        double actual = reservationPlace.getCostPerHour();

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
    @DisplayName("Изменение уникального идентификатора")
    void setId() {
        int expected = 4;

        reservationPlace.setId(expected);
        int actual = reservationPlace.getId();

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
        double expected = reservationPlace.getCostPerHour() + 4.6;

        reservationPlace.setCostPerHour(expected);
        double actual = reservationPlace.getCostPerHour();

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