package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.finder.ObjectFinderForTests;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.model.roles.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContainerConfig.class})
class ReservationDAOImplTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ReservationDAO reservationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ReservationPlaceDAO reservationPlaceDAO;

    private Reservation reservation;

    private ReservationPlace reservationPlace;

    private User user;

    @BeforeEach
    void init() {
        String username = "useruser654654";
        String password = "passwordpassword";
        userDAO.saveUser(username, password);
        user = userDAO.getUser(username, password);
        reservationPlace = new Workplace("009", 543, 34, 20);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, dataSource);
        reservation = new Reservation(1, user.getId(),
                LocalDateTime.now().withSecond(0).withNano(0),
                LocalDateTime.now().withSecond(0).withNano(0).plusHours(2), reservationPlace);
        reservationDAO.saveReservation(reservation);
        ObjectFinderForTests.setReservationIdFromDB(reservation, dataSource);
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
        ObjectFinderForTests.setReservationIdFromDB(reservation, dataSource);

        int actual = reservationDAO.getAllReservations().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней отсортированных по пользователям из бд")
    void getAllReservationsByUsers() {
        List<Long> expected = reservationDAO.getAllReservations().stream()
                .sorted(Comparator.comparing(Reservation::getUserId)).map(Reservation::getUserId).toList();

        List<Long> actual = reservationDAO.getAllReservationsByUsers().stream().map(Reservation::getUserId).toList();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected.isEmpty()).isFalse();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех броней отсортированных по датам из бд")
    void getAllReservationsByDate() {
        List<LocalDateTime> expected = reservationDAO.getAllReservations().stream()
                .sorted(Comparator.comparing(Reservation::getStartDate)).map(Reservation::getStartDate).toList();

        List<LocalDateTime> actual = reservationDAO.getAllReservationsByDate().stream().map(Reservation::getStartDate).toList();

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
        ObjectFinderForTests.setReservationIdFromDB(reservation, dataSource);
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