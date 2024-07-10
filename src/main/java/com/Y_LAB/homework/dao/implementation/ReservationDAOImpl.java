package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.dao.constants.SQLConstants.*;

/**
 * Класс ДАО слоя для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 2.0
 */
@Repository
@AllArgsConstructor
public class ReservationDAOImpl implements ReservationDAO {

    /** Поле для подключения к базе данных*/
    private final Connection connection;

    /** Поле ДАО слоя мест для бронирования*/
    private final ReservationPlaceDAO reservationPlaceDAO;

    public ReservationDAOImpl() {
        connection = ConnectionToDatabase.getConnection();
        reservationPlaceDAO = new ReservationPlaceDAOImpl();
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservations() {
        return getReservationsFromQuery(RESERVATION_GET_ALL);
    }

    /** Возвращает коллекцию броней из переданного SQL запроса
     * @param reservationGetAll SQL запрос
     * @return Коллекция броней найденная по переданному запросу
     */
    private List<Reservation> getReservationsFromQuery(String reservationGetAll) {
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            Statement statement = connection.createStatement();
            ResultSet reservationResultSet = statement.executeQuery(reservationGetAll);
            while (reservationResultSet.next()) {
                reservation = getReservationFromResultSet(reservationResultSet);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservations;
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsByUsers() {
        return getReservationsFromQuery(RESERVATION_GET_ALL_ORDER_BY_USER_ID);
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsByDate() {
        return getReservationsFromQuery(RESERVATION_GET_ALL_ORDER_BY_START_DATE);
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllReservationsWithReservationPlace(int reservationPlaceId) {
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_FIND_BY_RESERVATION_PLACE_ID);
            preparedStatement.setInt(1, reservationPlaceId);
            preparedStatement.execute();
            ResultSet reservationResultSet = preparedStatement.getResultSet();
            while (reservationResultSet.next()) {
                reservation = getReservationFromResultSet(reservationResultSet);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservations;
    }

    /** {@inheritDoc}*/
    @Override
    public List<Reservation> getAllUserReservations(long userId) {
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_FIND_BY_USER_ID);
            preparedStatement.setLong(1, userId);
            preparedStatement.execute();
            ResultSet reservationResultSet = preparedStatement.getResultSet();
            while (reservationResultSet.next()) {
                reservation = getReservationFromResultSet(reservationResultSet);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservations;
    }

    /**
     * Метод для получения объекта брони из {@link ResultSet}
     * @param reservationResultSet объект {@link ResultSet}
     * @throws SQLException Если есть проблемы при получении значений из колонок объекта {@link ResultSet}
     * @return объект брони
     */
    private Reservation getReservationFromResultSet(ResultSet reservationResultSet) throws SQLException {
        return Reservation.builder()
                .id(reservationResultSet.getLong(1))
                .userId(reservationResultSet.getLong(2))
                .startDate(reservationResultSet.getTimestamp(4).toLocalDateTime())
                .endDate(reservationResultSet.getTimestamp(5).toLocalDateTime())
                .reservationPlace(reservationPlaceDAO.getReservationPlace(reservationResultSet.getInt(3)))
                .build();
    }

    /** {@inheritDoc}*/
    @Override
    public Reservation getReservation(long id) {
        Reservation reservation = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_FIND_BY_ID);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            ResultSet reservationResultSet = preparedStatement.getResultSet();
            if(reservationResultSet.next()) {
                reservation = getReservationFromResultSet(reservationResultSet);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservation;
    }

    /** {@inheritDoc}*/
    @Override
    public Long getReservationId(Reservation reservation) {
        Long id = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_FIND_BY_FIELDS_WITHOUT_ID);
            preparedStatement.setLong(1, reservation.getUserId());
            preparedStatement.setInt(2, reservation.getReservationPlace().getId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reservation.getStartDate()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(reservation.getEndDate()));
            preparedStatement.execute();
            ResultSet reservationResultSet = preparedStatement.getResultSet();
            if(reservationResultSet.next()) {
                id = reservationResultSet.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return id;
    }

    /** {@inheritDoc}*/
    @Override
    public void saveReservation(Reservation reservation) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_FULL_INSERT);
            preparedStatement.setLong(1, reservation.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(reservation.getStartDate()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reservation.getEndDate()));
            preparedStatement.setInt(4, reservation.getReservationPlace().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void updateReservation(Reservation reservation) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_FULL_UPDATE);
            preparedStatement.setLong(1, reservation.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(reservation.getStartDate()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reservation.getEndDate()));
            preparedStatement.setInt(4, reservation.getReservationPlace().getId());
            preparedStatement.setLong(5, reservation.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteReservation(long id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(RESERVATION_DELETE_BY_ID);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }
}
