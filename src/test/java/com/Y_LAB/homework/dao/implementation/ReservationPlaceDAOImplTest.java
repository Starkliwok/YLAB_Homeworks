package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.entity.reservation.ConferenceRoom;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.Y_LAB.homework.constants.ReservationConstants.END_HOUR_FOR_RESERVATION;
import static com.Y_LAB.homework.constants.ReservationConstants.START_HOUR_FOR_RESERVATION;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationPlaceDAOImplTest {

    private ReservationPlaceDAOImpl reservationPlaceDAO;

    @BeforeEach
    void init() {
        reservationPlaceDAO = new ReservationPlaceDAOImpl();
    }

    @Test
    @DisplayName("Получение всех мест для бронирования")
    void getAllReservationPlaces() {
        int expected = reservationPlaceDAO.getAllReservationPlaces().size() + 1;
        ReservationPlace reservationPlace = new Workplace();
        reservationPlaceDAO.addReservationPlace(reservationPlace);

        int actual = reservationPlaceDAO.getAllReservationPlaces().size();

        assertThat(actual).isEqualTo(expected);
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
    }

    @Test
    @DisplayName("Получение всех мест для бронирования по типу")
    void getAllReservationPlacesByTypes() {
        int expected = reservationPlaceDAO.getAllReservationPlacesByTypes(new Workplace()).size() + 2;
        int expected2 = reservationPlaceDAO.getAllReservationPlacesByTypes(new ConferenceRoom()).size() + 1;
        ReservationPlace reservationPlace = new Workplace();
        ReservationPlace reservationPlace1 = new Workplace();
        ReservationPlace reservationPlace2 = new ConferenceRoom();
        reservationPlaceDAO.addReservationPlace(reservationPlace);
        reservationPlaceDAO.addReservationPlace(reservationPlace1);
        reservationPlaceDAO.addReservationPlace(reservationPlace2);

        int actual = reservationPlaceDAO.getAllReservationPlacesByTypes(new Workplace()).size();
        int actual2 = reservationPlaceDAO.getAllReservationPlacesByTypes(new ConferenceRoom()).size();

        assertThat(actual).isEqualTo(expected);
        assertThat(actual2).isEqualTo(expected2);
        assertThat(actual).isNotEqualTo(expected2);
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace1.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace2.getId());
    }

    @Test
    @DisplayName("Получение всех доступных мест для бронирования")
    void getAllAvailableReservations() {
        int expected = reservationPlaceDAO.getAllAvailableReservations().size() + 1;
        reservationPlaceDAO.addReservationPlace(new Workplace());

        int actual = reservationPlaceDAO.getAllAvailableReservations().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех доступных дат для бронирования места")
    void getAllAvailableDatesForReservePlace() {
        ReservationPlace reservationPlace = new Workplace("001", 1.74, 17.4);
        ReservationService reservationService = new ReservationServiceImpl();
        ReservationPlaceService reservationPlaceService = new ReservationPlaceServiceImpl();
        UserService userService = new UserServiceImpl();
        reservationPlaceService.addReservationPlace(reservationPlace);
        User user = new User("user", "user");
        userService.addUserToUserSet(user);
        Reservation reservation = new Reservation();
        reservation.setReservationPlace(reservationPlace);
        reservation.setUser(userService.getUserFromUserSet(user.getUsername(), user.getPassword()));
        reservation.setStartDate(Date.from(LocalDateTime.of(2024, 6, 22, 8, 0).atZone(ZoneId.systemDefault()).toInstant()));
        reservation.setEndDate(Date.from(LocalDateTime.of(2024, 6, 22, 22, 0).atZone(ZoneId.systemDefault()).toInstant()));
        int expected = reservationPlaceDAO
                .getAllAvailableDatesForReservePlace(reservationPlace).size() - (END_HOUR_FOR_RESERVATION - START_HOUR_FOR_RESERVATION) - 1;
        reservationService.addReservation(reservation);

        int actual = reservationPlaceDAO.getAllAvailableDatesForReservePlace(reservationPlace).size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение места для бронирования")
    void getReservationPlace() {
        ReservationPlace reservationPlace = new Workplace("001", 1.74, 17.4);

        ReservationPlace actual1 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.addReservationPlace(reservationPlace);
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual1).isNull();
        assertThat(actual2).isNotNull();
        assertThat(actual2).isEqualTo(reservationPlace);
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
    }

    @Test
    @DisplayName("Добавление места для бронирования")
    void addReservationPlace() {
        ReservationPlace reservationPlace = new Workplace("001", 1.74, 17.4);

        ReservationPlace actual1 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.addReservationPlace(reservationPlace);
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual1).isNull();
        assertThat(actual2).isNotNull();
        assertThat(actual2).isEqualTo(reservationPlace);
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
    }

    @Test
    @DisplayName("Обновление места для бронирования")
    void updateReservationPlace() {
        String newName = "002";
        ReservationPlace reservationPlace = new Workplace("001", 1.74, 17.4);
        reservationPlaceDAO.addReservationPlace(reservationPlace);

        String actual1 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId()).getName();
        reservationPlace.setName(newName);
        reservationPlaceDAO.updateReservationPlace(reservationPlace);
        String actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId()).getName();

        assertThat(actual1).isNotEqualTo(newName);
        assertThat(actual2).isEqualTo(newName);
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
    }

    @Test
    @DisplayName("Удаление места для бронирования")
    void deleteReservationPlace() {
        ReservationPlace reservationPlace = new Workplace("001", 1.74, 17.4);

        reservationPlaceDAO.addReservationPlace(reservationPlace);
        ReservationPlace actual1 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual1).isEqualTo(reservationPlace);
        assertThat(actual2).isNull();
    }
}