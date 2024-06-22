package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.service.ReservationPlaceService;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 1.0
 */
@AllArgsConstructor
public class ReservationPlaceServiceImpl implements ReservationPlaceService {

    /**Поле ДАО слоя для взаимодействия с местами для бронирований*/
    private final ReservationPlaceDAO reservationPlaceDAO;

    public ReservationPlaceServiceImpl() {
        reservationPlaceDAO = new ReservationPlaceDAOImpl();
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#getAllReservationPlaces} для получения всех мест для бронирования
     * @return Коллекция всех мест для бронирования
     */
    @Override
    public List<ReservationPlace> getAllReservationPlaces() {
        return reservationPlaceDAO.getAllReservationPlaces();
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#getAllReservationPlacesByTypes} для получения мест для бронирования по типу места
     * @param reservationPlace объект места для бронирования
     * @return Коллекция мест для бронирования определённого типа
     */
    @Override
    public List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace) {
        return reservationPlaceDAO.getAllReservationPlacesByTypes(reservationPlace);
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#getAllAvailableReservations} для получения мест и доступных дат для
     * бронирования этого места
     * @return Коллекция мест и доступных дат для бронирования места
     */
    @Override
    public Map<ReservationPlace, List<Date>> getAllAvailableReservations() {
        return reservationPlaceDAO.getAllAvailableReservations();
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#getAllAvailableDatesForReservePlace} для получения доступных дат
     * для бронирования места
     * @param reservationPlace объект места для бронирования
     * @return Коллекция доступных дат для бронирования места
     */
    @Override
    public List<Date> getAllAvailableDatesForReservePlace(ReservationPlace reservationPlace) {
        return reservationPlaceDAO.getAllAvailableDatesForReservePlace(reservationPlace);
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#getReservationPlace} для получения объекта места для бронирования
     * @param id уникальный идентификатор места
     * @return Объект места для бронирования
     */
    @Override
    public ReservationPlace getReservationPlace(int id) {
        return reservationPlaceDAO.getReservationPlace(id);
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#addReservationPlace} для сохранения объекта места для бронирования
     * @param reservationPlace объект места для бронирования
     */
    @Override
    public void addReservationPlace(ReservationPlace reservationPlace) {
        reservationPlaceDAO.addReservationPlace(reservationPlace);
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#updateReservationPlace} для обновления места для бронирования
     * @param reservationPlace объект места для бронирования
     */
    @Override
    public void updateReservationPlace(ReservationPlace reservationPlace) {
        reservationPlaceDAO.updateReservationPlace(reservationPlace);
    }

    /**
     * Метод вызывает {@link ReservationPlaceDAO#deleteReservationPlace} для удаления места для бронирования
     * @param id уникальный идентификатор места для бронирования
     */
    @Override
    public void deleteReservationPlace(int id) {
        reservationPlaceDAO.deleteReservationPlace(id);
    }
}
