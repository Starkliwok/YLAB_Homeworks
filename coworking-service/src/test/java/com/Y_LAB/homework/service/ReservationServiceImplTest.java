package com.Y_LAB.homework.service;

import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationDAOImpl reservationDAO;

    @Mock
    private ReservationPlaceServiceImpl reservationPlaceService;

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
    void getAllUserReservations() throws ObjectNotFoundException {
        int userId = 432;
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(new Reservation());
        when(reservationDAO.getAllUserReservations(userId)).thenReturn(reservationList);

        reservationService.getAllUserReservations(userId);

        verify(reservationDAO, times(1)).getAllUserReservations(userId);
    }

    @Test
    @DisplayName("Проверка на вызов метода получения брони")
    void getReservation() throws ObjectNotFoundException {
        int id = 1;
        Reservation reservation = new Reservation();
        when(reservationDAO.getReservation(id)).thenReturn(reservation);

        reservationService.getReservation(id);

        verify(reservationDAO, times(1)).getReservation(id);
    }

    @Test
    @DisplayName("Проверка на вызов метода добавления брони")
    void addReservation() throws ObjectNotFoundException {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        long userId = 1;
        when(reservationPlaceService.getReservationPlace(reservationRequestDTO.getReservationPlaceId())).thenReturn(new Workplace());

        reservationService.saveReservation(reservationRequestDTO, userId);

        verify(reservationPlaceService, times(1)).getReservationPlace(reservationRequestDTO.getReservationPlaceId());
    }

    @Test
    @DisplayName("Проверка на вызов метода обновления брони")
    void updateReservation() throws ObjectNotFoundException {
        Reservation reservation = new Reservation();
        reservation.setId(1);
        ReservationPlace reservationPlace = new Workplace();
        reservationPlace.setId(2);
        reservation.setReservationPlace(reservationPlace);
        when(reservationDAO.getReservation(reservation.getId())).thenReturn(reservation);
        when(reservationPlaceService.getReservationPlace(reservation.getReservationPlace().getId())).thenReturn(new Workplace());

        reservationService.updateReservation(reservation);

        verify(reservationDAO, times(1)).updateReservation(reservation);
    }

    @Test
    @DisplayName("Проверка на вызов метода удаления брони")
    void deleteReservation() throws ObjectNotFoundException {
        Reservation reservation = new Reservation();
        int reservationId = 2;
        when(reservationDAO.getReservation(reservationId)).thenReturn(reservation);

        reservationService.deleteReservation(reservationId);

        verify(reservationDAO, times(1)).deleteReservation(reservationId);
    }
}