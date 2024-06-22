package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.service.ReservationService;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Сервис для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 1.0
 */
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    /**Поле ДАО слоя для взаимодействия с бронированиями*/
    private final ReservationDAO reservationDAO;

    public ReservationServiceImpl() {
        reservationDAO = new ReservationDAOImpl();
    }

    /**
     * Метод вызывает {@link ReservationDAO#getAllReservations} для получения коллекции всех броней
     * @return коллекция всех броней
     */
    @Override
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    /**
     * Метод вызывает {@link ReservationDAO#getAllReservationsByUsers} для получения коллекции всех броней отсортированных
     * по пользователю
     * @return коллекция всех броней отсортированных по пользователю
     */
    @Override
    public List<Reservation> getAllReservationsByUsers() {
        return reservationDAO.getAllReservationsByUsers();
    }

    /**
     * Метод вызывает {@link ReservationDAO#getAllReservationsByDate} для получения коллекции всех броней отсортированных
     * по дате
     * @return коллекция всех броней отсортированных по дате
     */
    @Override
    public List<Reservation> getAllReservationsByDate() {
        return reservationDAO.getAllReservationsByDate();
    }

    /**
     * Метод вызывает {@link ReservationDAO#getAllUserReservations} для получения коллекции всех броней пользователя
     * @param user объект пользователя
     * @return коллекция всех броней пользователя
     */
    @Override
    public List<Reservation> getAllUserReservations(User user) {
        return reservationDAO.getAllUserReservations(user);
    }

    /**
     * Метод вызывает {@link ReservationDAO#getReservation} для получения брони по уникальному идентификатору
     * @param id уникальный идентификатор брони
     * @return объект брони
     */
    @Override
    public Reservation getReservation(int id) {
        return reservationDAO.getReservation(id);
    }

    /**
     * Метод вызывает {@link ReservationDAO#addReservation} для сохранения объекта брони
     * @param reservation объект брони
     */
    @Override
    public void addReservation(Reservation reservation) {
        reservationDAO.addReservation(reservation);
    }

    /**
     * Метод вызывает {@link ReservationDAO#updateReservation} для обновления брони
     * @param reservation объект брони
     */
    @Override
    public void updateReservation(Reservation reservation) {
        reservationDAO.updateReservation(reservation);
    }

    /**
     * Метод вызывает {@link ReservationDAO#deleteReservation}для удаления брони
     * @param id уникальный идентификатор брони
     */
    @Override
    public void deleteReservation(int id) {
        reservationDAO.deleteReservation(id);
    }
}
