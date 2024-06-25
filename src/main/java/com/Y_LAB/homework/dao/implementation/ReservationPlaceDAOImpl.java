package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.Y_LAB.homework.constants.ReservationConstants.*;

/**
 * Класс ДАО слоя для взаимодействия с местами для бронирований
 * @author Денис Попов
 * @version 1.0
 */
public class ReservationPlaceDAOImpl implements ReservationPlaceDAO {

    /** Статическое поле которое содержит в себе все места для бронирований.*/
    private static final List<ReservationPlace> allReservationPlaces = new ArrayList<>();

    public ReservationPlaceDAOImpl() {
    }

    /** {@inheritDoc}*/
    @Override
    public List<ReservationPlace> getAllReservationPlaces() {
        return allReservationPlaces;
    }

    /** {@inheritDoc}*/
    @Override
    public List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace) {
        return allReservationPlaces.stream().filter(x -> x.getClass() == reservationPlace.getClass()).collect(Collectors.toList());
    }

    /** {@inheritDoc}*/
    @Override
    public Map<ReservationPlace, List<LocalDateTime>> getAllAvailableReservations() {
        Map<ReservationPlace, List<LocalDateTime>> availableReservations = new HashMap<>();

        for(ReservationPlace reservationPlace : allReservationPlaces) {
            List<LocalDateTime> allAvailableDatesForReservePlace = getAllAvailableDatesForReservePlace(reservationPlace);
            availableReservations.put(reservationPlace, allAvailableDatesForReservePlace);
        }
        return availableReservations;
    }

    /**
     * Метод для получения доступных дат для бронирования места.
     * Метод получает бронирования всех пользователей и заполняет доступные даты на
     * {@link com.Y_LAB.homework.constants.ReservationConstants#RESERVATION_PERIOD}
     * дней вперёд, после чего исключает доступные даты для бронирования, в случае если те
     * уже заняты, а так же исключает даты, если в них нет временных промежутков для бронирования.
     * Метод исключает временные промежутки которые не имеют пары, то есть не могут быть зарезервированы под бронь
     *
     * @param reservationPlace объект места для бронирования
     * @return Коллекция доступных дат для бронирования места
     */
    @Override
    public List<LocalDateTime> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace) {
        ReservationService reservationService = new ReservationServiceImpl();
        List<Reservation> allReservations = reservationService.getAllReservations();
        List<LocalDateTime> availableDates = new ArrayList<>();
        for(int i = 0; i <= RESERVATION_PERIOD; i++) {
            for(int j = START_HOUR_FOR_RESERVATION; j <= END_HOUR_FOR_RESERVATION; j++) {
                availableDates.add(
                        LocalDateTime.now()
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                        .withHour(j).plusDays(i));
            }
        }
        for(Reservation reservation : allReservations) {
            if(reservationPlace.equals(reservation.getReservationPlace())) {
                long durationInHours = Duration.between(reservation.getStartDate(), reservation.getEndDate()).toHours();
                for(int i = 0; i < durationInHours; i++) {
                    availableDates.remove(reservation.getEndDate().minusHours(i + 1));
                }
                List<LocalDateTime> datesToRemove = availableDates.stream()
                        .filter(x -> x.isBefore(reservation.getEndDate().withHour(23)))
                        .toList();
                if(datesToRemove.size() > 2) {
                    for (int i = 1; i < datesToRemove.size(); i++) {
                        if (datesToRemove.get(i - 1).getHour() != datesToRemove.get(i).getHour() - 1) {
                            if (i == 1) {
                                availableDates.remove(datesToRemove.get(0));
                            } else {
                                availableDates.remove(datesToRemove.get(i));
                            }
                        }
                    }
                } else {
                    availableDates.remove(datesToRemove.get(0));
                }
            }
        }
        return availableDates;
    }

    /** {@inheritDoc}*/
    @Override
    public ReservationPlace getReservationPlace(int id) {
        List<ReservationPlace> filterReservationPlace = allReservationPlaces.stream().filter(x -> x.getId() == id).toList();
        return filterReservationPlace.isEmpty() ? null : filterReservationPlace.get(0);
    }

    /** {@inheritDoc}*/
    @Override
    public void addReservationPlace(ReservationPlace reservationPlace) {
        if(getReservationPlace(reservationPlace.getId()) == null) {
            allReservationPlaces.add(reservationPlace);
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void updateReservationPlace(ReservationPlace reservationPlace) {
        deleteReservationPlace(reservationPlace.getId());
        allReservationPlaces.add(reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteReservationPlace(int id) {
        allReservationPlaces.remove(getReservationPlace(id));
    }
}
