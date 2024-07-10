package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.service.ReservationPlaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для взаимодействия с местами для бронирований
 * @author Денис Попов
 * @version 2.0
 */
@Service
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
    public ReservationPlace getReservationPlace(int id) {
        return reservationPlaceDAO.getReservationPlace(id);
    }

    /** {@inheritDoc}*/
    @Override
    public void saveReservationPlace(ReservationPlace reservationPlace) {
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public void saveReservationPlace(ReservationPlaceRequestDTO reservationPlaceRequestDTO) {
        ReservationPlace reservationPlace;
        if(reservationPlaceRequestDTO.getTypeId() == 1) {
            reservationPlace = new ConferenceRoom(reservationPlaceRequestDTO.getName(), reservationPlaceRequestDTO.getPlaceArea(),
                    reservationPlaceRequestDTO.getCostPerHour(), reservationPlaceRequestDTO.getNumberOfSeats());
        } else {
            reservationPlace = new Workplace(reservationPlaceRequestDTO.getName(), reservationPlaceRequestDTO.getPlaceArea(),
                    reservationPlaceRequestDTO.getCostPerHour(), reservationPlaceRequestDTO.getNumberOfSeats());
        }
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
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
