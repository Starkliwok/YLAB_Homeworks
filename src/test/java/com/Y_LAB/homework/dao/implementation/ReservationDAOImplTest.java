package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.entity.roles.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDAOImplTest {

    private ReservationDAO reservationDAO;

    private ReservationPlaceDAO reservationPlaceDAO;

    private UserDAO userDAO;

    private Reservation reservation;

    private User user;

    @BeforeEach
    void init() {
        reservationDAO = new ReservationDAOImpl();
        reservationPlaceDAO = new ReservationPlaceDAOImpl();
        userDAO = new UserDAOImpl();
        reservation = new Reservation();
        ReservationPlace reservationPlace = new Workplace("001", 1.74, 17.4);
        reservationPlaceDAO.addReservationPlace(reservationPlace);
        user = new User("user", "user");
        userDAO.addUserToUserSet(user);
        reservation.setReservationPlace(reservationPlace);
        reservation.setUser(userDAO.getUserFromUserSet(user.getUsername(), user.getPassword()));
        reservation.setStartDate(Date.from(LocalDateTime.of(2024, 6, 22, 8, 0).atZone(ZoneId.systemDefault()).toInstant()));
        reservation.setEndDate(Date.from(LocalDateTime.of(2024, 6, 22, 22, 0).atZone(ZoneId.systemDefault()).toInstant()));
    }

    @AfterEach
    void clear() {
        reservationDAO.deleteReservation(reservation.getId());
        userDAO.deleteUser(user);
        reservationPlaceDAO.deleteReservationPlace(reservation.getReservationPlace().getId());
        reservationDAO.getAllReservations().clear();
        userDAO.getUserSet().clear();
    }

    @Test
    @DisplayName("Получение всех броней")
    void getAllReservations() {
        Reservation reservation1 = new Reservation();
        int expected = reservationDAO.getAllReservations().size() + 1;
        reservationDAO.addReservation(reservation1);

        int actual = reservationDAO.getAllReservations().size();

        assertThat(actual).isEqualTo(expected);
        reservationDAO.deleteReservation(reservation1.getId());
    }

    @Test
    @DisplayName("Получение всех броней отсортированных по пользователям")
    void getAllReservationsByUsers() {
        reservationDAO.addReservation(reservation);
        List<Reservation> expected = reservationDAO.getAllReservations().stream()
                .sorted(Comparator.comparing(o -> o.getUser().getId())).toList();

        List<Reservation> actual = reservationDAO.getAllReservationsByUsers();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected.isEmpty()).isFalse();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней отсортированных по датам")
    void getAllReservationsByDate() {
        reservationDAO.addReservation(reservation);
        List<Reservation> expected = reservationDAO.getAllReservations().stream()
                .sorted(Comparator.comparing(Reservation::getStartDate)).collect(Collectors.toList());

        List<Reservation> actual = reservationDAO.getAllReservationsByDate();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected.isEmpty()).isFalse();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней пользователя")
    void getAllUserReservations() {
        reservationDAO.getAllReservations().clear();
        reservationDAO.addReservation(reservation);
        List<Reservation> expected = reservationDAO.getAllReservations().stream().filter(x -> x.getUser().equals(user)).toList();

        List<Reservation> actual = reservationDAO.getAllUserReservations(user);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение брони")
    void getReservation() {
        Reservation actual1 = reservationDAO.getReservation(reservation.getId());
        reservationDAO.addReservation(reservation);

        Reservation actual2 = reservationDAO.getReservation(reservation.getId());

        assertThat(actual1).isNull();
        assertThat(actual2).isEqualTo(reservation);
    }

    @Test
    @DisplayName("Добавление брони")
    void addReservation() {
        Reservation actual1 = reservationDAO.getReservation(reservation.getId());
        reservationDAO.addReservation(reservation);

        Reservation actual2 = reservationDAO.getReservation(reservation.getId());

        assertThat(actual1).isNull();
        assertThat(actual2).isEqualTo(reservation);
    }

    @Test
    @DisplayName("Обновление брони")
    void updateReservation() {
        reservationDAO.addReservation(reservation);
        ReservationPlace actual1 = reservationDAO.getReservation(reservation.getId()).getReservationPlace();
        reservation.setReservationPlace(new Workplace());

        reservationDAO.updateReservation(reservation);
        Reservation actual2 = reservationDAO.getReservation(reservation.getId());

        assertThat(actual1).isNotEqualTo(reservation.getReservationPlace());
        assertThat(reservation).isNotNull();
        assertThat(actual2).isEqualTo(reservation);
    }

    @Test
    @DisplayName("Удаление брони")
    void deleteReservation() {
        reservationDAO.addReservation(reservation);
        Reservation actual1 = reservationDAO.getReservation(reservation.getId());
        reservationDAO.deleteReservation(reservation.getId());
        Reservation actual2 = reservationDAO.getReservation(reservation.getId());

        assertThat(actual1).isEqualTo(reservation);
        assertThat(reservation).isNotNull();
        assertThat(actual2).isNull();
    }
}