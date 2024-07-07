package com.Y_LAB.homework.service;

import com.Y_LAB.homework.model.reservation.ReservationPlace;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface FreeReservationSlotService {
    Map<ReservationPlace, List<LocalDate>> getAllAvailablePlaceForReservations();

    List<LocalDate> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace);

    Map<LocalTime, LocalTime> getAllAvailableTimesForReservation(ReservationPlace reservationPlace, LocalDate localDate);

    List<String> getAllAvailableTimeListForReservation(ReservationPlace reservationPlace, LocalDate localDate);

    List<ReservationPlace> getAllAvailablePlacesForReserveDate(LocalDate localDate);
}
