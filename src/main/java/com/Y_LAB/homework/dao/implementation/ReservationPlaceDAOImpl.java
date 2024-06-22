package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    /**
     * Метод для получения всех мест для бронирования из {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @return Коллекция всех мест для бронирования
     */
    @Override
    public List<ReservationPlace> getAllReservationPlaces() {
        return allReservationPlaces;
    }

    /**
     * Метод для получения мест для бронирования по типу места из {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @param reservationPlace объект места для бронирования
     * @return Коллекция мест для бронирования определённого типа
     */
    @Override
    public List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace) {
        return allReservationPlaces.stream().filter(x -> x.getClass() == reservationPlace.getClass()).collect(Collectors.toList());
    }

    /**
     * Метод для получения мест и доступных дат для бронирования этого места из
     * {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @return Коллекция мест и доступных дат для бронирования места
     */
    @Override
    public Map<ReservationPlace, List<Date>> getAllAvailableReservations() {
        Map<ReservationPlace, List<Date>> availableReservations = new HashMap<>();

        for(ReservationPlace reservationPlace : allReservationPlaces) {
            List<Date> allAvailableDatesForReservePlace = getAllAvailableDatesForReservePlace(reservationPlace);
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
     * @param reservationPlace объект места для бронирования
     * @return Коллекция доступных дат для бронирования места
     */
    @Override
    public List<Date> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace) {
        List<Reservation> allReservations = new ReservationServiceImpl().getAllReservations();
        List<Date> availableDates = new ArrayList<>();

        for(int i = 0; i <= RESERVATION_PERIOD; i++) {
            for(int j = START_HOUR_FOR_RESERVATION; j <= END_HOUR_FOR_RESERVATION; j++) {
                LocalDateTime localDateTime = LocalDateTime.now()
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                        .withDayOfMonth(LocalDate.now().getDayOfMonth() + i)
                        .withHour(j);
                availableDates.add(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
        for(Reservation reservation : allReservations) {
            if(reservationPlace.equals(reservation.getReservationPlace())) {
                LocalDateTime localStartDate = reservation.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime localEndDate = reservation.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                long durationInHours = Duration.between(localStartDate, localEndDate).toHours();
                for(int i = 0; i < durationInHours; i++) {
                    availableDates.remove(Date.from(localEndDate.minusHours(i + 1).atZone(ZoneId.systemDefault()).toInstant()));
                }
                List<Date> datesToRemove = availableDates.stream()
                        .filter(x -> x.before(Date.from(localEndDate.withHour(23).atZone(ZoneId.systemDefault()).toInstant())))
                        .toList();
                if(datesToRemove.size() > 2) {
                    for (int i = 1; i < datesToRemove.size(); i++) {
                        LocalDateTime localDateTime1 = datesToRemove.get(i - 1).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        LocalDateTime localDateTime2 = datesToRemove.get(i).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                        if (localDateTime1.getHour() != localDateTime2.getHour() - 1) {
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

    /**
     * Метод для получения объекта места для бронирования из {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @param id уникальный идентификатор места
     * @return Объект места для бронирования
     */
    @Override
    public ReservationPlace getReservationPlace(int id) {
        List<ReservationPlace> filterReservationPlace = allReservationPlaces.stream().filter(x -> x.getId() == id).toList();
        return filterReservationPlace.isEmpty() ? null : filterReservationPlace.get(0);
    }

    /**
     * Метод для добавления места для бронирования в {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @param reservationPlace объект места для бронирования
     */
    @Override
    public void addReservationPlace(ReservationPlace reservationPlace) {
        if(getReservationPlace(reservationPlace.getId()) == null) {
            allReservationPlaces.add(reservationPlace);
        }
    }

    /**
     * Метод для обновления места для бронирования в {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @param reservationPlace объект места для бронирования
     */
    @Override
    public void updateReservationPlace(ReservationPlace reservationPlace) {
        deleteReservationPlace(reservationPlace.getId());
        allReservationPlaces.add(reservationPlace);
    }

    /**
     * Метод для удаления места для бронирования в {@link ReservationPlaceDAOImpl#allReservationPlaces}
     * @param id уникальный идентификатор места для бронирования
     */
    @Override
    public void deleteReservationPlace(int id) {
        allReservationPlaces.remove(getReservationPlace(id));
    }
}
