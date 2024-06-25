package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.service.ReservationPlaceService;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 1.0
 *
 */
@AllArgsConstructor
public class ReservationPlaceServiceImpl implements ReservationPlaceService {

    /**Поле ДАО слоя для взаимодействия с местами для бронирований*/
    private final ReservationPlaceDAO reservationPlaceDAO;

    public ReservationPlaceServiceImpl() {
        reservationPlaceDAO = new ReservationPlaceDAOImpl();
    }

    /** {@inheritDoc}*/
    @Override
    public List<ReservationPlace> getAllReservationPlaces() {
        return reservationPlaceDAO.getAllReservationPlaces();
    }

    /** {@inheritDoc}*/
    @Override
    public List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace) {
        return reservationPlaceDAO.getAllReservationPlacesByTypes(reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public Map<ReservationPlace, List<LocalDateTime>> getAllAvailableReservations() {
        return reservationPlaceDAO.getAllAvailableReservations();
    }

    /** {@inheritDoc}*/
    @Override
    public List<LocalDateTime> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace) {
        return reservationPlaceDAO.getAllAvailableDatesForReservePlace(reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public ReservationPlace getReservationPlace(int id) {
        return reservationPlaceDAO.getReservationPlace(id);
    }

    /** {@inheritDoc}*/
    @Override
    public void addReservationPlace(ReservationPlace reservationPlace) {
        reservationPlaceDAO.addReservationPlace(reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public void updateReservationPlace(ReservationPlace reservationPlace) {
        reservationPlaceDAO.updateReservationPlace(reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteReservationPlace(int id) {
        reservationPlaceDAO.deleteReservationPlace(id);
    }
}
