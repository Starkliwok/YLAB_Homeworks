package com.Y_LAB.homework.service;

import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.model.reservation.Reservation;
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
    @DisplayName("Проверка на вызов метода получения всех броней")
    void getAllReservations() {
        reservationService.getAllReservations();

        verify(reservationDAO, times(1)).getAllReservations();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех броней отсортированных по пользователям")
    void getAllReservationsByUsers() {
        reservationService.getAllReservationsByUsers();

        verify(reservationDAO, times(1)).getAllReservationsByUsers();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех броней отсортированных по датам")
    void getAllReservationsByDate() {
        reservationService.getAllReservationsByDate();

        verify(reservationDAO, times(1)).getAllReservationsByDate();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех броней по id места для бронирования")
    void getAllReservationsWithReservationPlace() {
        int reservationPlaceId = 43;

        reservationService.getAllReservationsWithReservationPlace(reservationPlaceId);

        verify(reservationDAO, times(1)).getAllReservationsWithReservationPlace(reservationPlaceId);
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех броней пользователя")
    void getAllUserReservations() {
        int userId = 432;

        reservationService.getAllUserReservations(userId);

        verify(reservationDAO, times(1)).getAllUserReservations(userId);
    }

    @Test
    @DisplayName("Проверка на вызов метода получения брони")
    void getReservation() {
        int id = 1;

        reservationService.getReservation(id);

        verify(reservationDAO, times(1)).getReservation(id);
    }

    @Test
    @DisplayName("Проверка на вызов метода добавления брони")
    void addReservation() {
        Reservation reservation = new Reservation();

        reservationService.saveReservation(reservation);

        verify(reservationDAO, times(1)).saveReservation(reservation);
    }

    @Test
    @DisplayName("Проверка на вызов метода обновления брони")
    void updateReservation() {
        Reservation reservation = new Reservation();

        reservationService.updateReservation(reservation);

        verify(reservationDAO, times(1)).updateReservation(reservation);
    }

    @Test
    @DisplayName("Проверка на вызов метода удаления брони")
    void deleteReservation() {
        int reservationId = 2;

        reservationService.deleteReservation(reservationId);

        verify(reservationDAO, times(1)).deleteReservation(reservationId);
    }
}