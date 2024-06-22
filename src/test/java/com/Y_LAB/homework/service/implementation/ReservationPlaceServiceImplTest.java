package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceServiceImplTest {

    @Mock
    private ReservationPlaceDAOImpl reservationPlaceDAO;

    @InjectMocks
    private ReservationPlaceServiceImpl reservationPlaceService;

    @Test
    @DisplayName("Проверка на вызов метода получения всех мест для бронирования")
    void getAllReservationPlaces() {
        reservationPlaceService.getAllReservationPlaces();

        verify(reservationPlaceDAO, times(1)).getAllReservationPlaces();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех мест для бронирования по типу")
    void getAllReservationPlacesByTypes() {
        ReservationPlace reservationPlace = new Workplace();

        reservationPlaceService.getAllReservationPlacesByTypes(reservationPlace);

        verify(reservationPlaceDAO, times(1)).getAllReservationPlacesByTypes(reservationPlace);
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех доступных мест для бронирования")
    void getAllAvailableReservations() {
        reservationPlaceService.getAllAvailableReservations();

        verify(reservationPlaceDAO, times(1)).getAllAvailableReservations();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения всех доступных дат для бронирования места")
    void getAllAvailableDatesForReservePlace() {
        ReservationPlace reservationPlace = new Workplace();

        reservationPlaceService.getAllAvailableDatesForReservePlace(reservationPlace);

        verify(reservationPlaceDAO, times(1)).getAllAvailableDatesForReservePlace(reservationPlace);
    }

    @Test
    @DisplayName("Проверка на вызов метода получения места для бронирования")
    void getReservationPlace() {
        int id = 1;

        reservationPlaceService.getReservationPlace(id);

        verify(reservationPlaceDAO, times(1)).getReservationPlace(id);
    }

    @Test
    @DisplayName("Проверка на вызов метода добавления места для бронирования")
    void addReservationPlace() {
        ReservationPlace reservationPlace = new Workplace();

        reservationPlaceService.addReservationPlace(reservationPlace);

        verify(reservationPlaceDAO, times(1)).addReservationPlace(reservationPlace);
    }

    @Test
    @DisplayName("Проверка на вызов метода обновления места для бронирования")
    void updateReservationPlace() {
        ReservationPlace reservationPlace = new Workplace();

        reservationPlaceService.updateReservationPlace(reservationPlace);

        verify(reservationPlaceDAO, times(1)).updateReservationPlace(reservationPlace);
    }

    @Test
    @DisplayName("Проверка на вызов метода удаления места для бронирования")
    void deleteReservationPlace() {
        ReservationPlace reservationPlace = new Workplace();

        reservationPlaceService.deleteReservationPlace(reservationPlace.getId());

        verify(reservationPlaceDAO, times(1)).deleteReservationPlace(reservationPlace.getId());
    }
}