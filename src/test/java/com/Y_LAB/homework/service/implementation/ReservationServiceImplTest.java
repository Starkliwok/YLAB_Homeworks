package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationDAOImpl reservationDAO;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    @DisplayName("Вызов метода получения всех броней")
    void getAllReservations() {
        reservationService.getAllReservations();

        verify(reservationDAO, times(1)).getAllReservations();
    }

    @Test
    @DisplayName("Вызов метода получения всех броней отсортированных по пользователям")
    void getAllReservationsByUsers() {
        reservationService.getAllReservationsByUsers();

        verify(reservationDAO, times(1)).getAllReservationsByUsers();
    }

    @Test
    @DisplayName("Вызов метода получения всех броней отсортированных по датам")
    void getAllReservationsByDate() {
        reservationService.getAllReservationsByDate();

        verify(reservationDAO, times(1)).getAllReservationsByDate();
    }

    @Test
    @DisplayName("Вызов метода получения всех броней пользователя")
    void getAllUserReservations() {
        User user = new User("user", "password");

        reservationService.getAllUserReservations(user);

        verify(reservationDAO, times(1)).getAllUserReservations(user);
    }

    @Test
    @DisplayName("Вызов метода получения брони")
    void getReservation() {
        int id = 1;

        reservationService.getReservation(id);

        verify(reservationDAO, times(1)).getReservation(id);
    }

    @Test
    @DisplayName("Вызов метода добавления брони")
    void addReservation() {
        Reservation reservation = new Reservation();

        reservationService.addReservation(reservation);

        verify(reservationDAO, times(1)).addReservation(reservation);
    }

    @Test
    @DisplayName("Вызов метода обновления брони")
    void updateReservation() {
        Reservation reservation = new Reservation();

        reservationService.updateReservation(reservation);

        verify(reservationDAO, times(1)).updateReservation(reservation);
    }

    @Test
    @DisplayName("Вызов метода удаления брони")
    void deleteReservation() {
        Reservation reservation = new Reservation();

        reservationService.deleteReservation(reservation.getId());

        verify(reservationDAO, times(1)).deleteReservation(reservation.getId());
    }
}