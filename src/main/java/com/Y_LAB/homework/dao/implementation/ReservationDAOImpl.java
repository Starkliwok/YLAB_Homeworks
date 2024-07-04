package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Класс ДАО слоя для взаимодействия с бронированиями
 * @author Денис Попов
 * @version 2.0
 */
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
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            Statement statement = connection.createStatement();
            ResultSet reservationResultSet = statement.executeQuery(
                    "SELECT * FROM coworking.reservation");
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
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            Statement statement = connection.createStatement();
            ResultSet reservationResultSet = statement.executeQuery(
                    "SELECT * FROM coworking.reservation ORDER BY user_id");
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
    public List<Reservation> getAllReservationsByDate() {
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            Statement statement = connection.createStatement();
            ResultSet reservationResultSet = statement.executeQuery(
                    "SELECT * FROM coworking.reservation ORDER BY start_date");
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
    public List<Reservation> getAllReservationsWithReservationPlace(int reservationPlaceId) {
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, user_id, reservation_place_id, start_date, end_date " +
                            "FROM coworking.reservation WHERE reservation_place_id = ?");
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
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, user_id, reservation_place_id, start_date, end_date " +
                            "FROM coworking.reservation WHERE user_id = ?");
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
        long id = reservationResultSet.getLong(1);
        long userID = reservationResultSet.getLong(2);
        int reservationPlaceId = reservationResultSet.getInt(3);
        LocalDateTime startDate = reservationResultSet.getTimestamp(4).toLocalDateTime();
        LocalDateTime endDate = reservationResultSet.getTimestamp(5).toLocalDateTime();
        ReservationPlace reservationPlace = reservationPlaceDAO.getReservationPlace(reservationPlaceId);

        return new Reservation(id, userID, startDate, endDate, reservationPlace);
    }

    /** {@inheritDoc}*/
    @Override
    public Reservation getReservation(long id) {
        Reservation reservation = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, user_id, reservation_place_id, start_date, end_date " +
                            "FROM coworking.reservation WHERE id = ?");
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
    public void saveReservation(Reservation reservation) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "INSERT INTO coworking.reservation (user_id, start_date, end_date, reservation_place_id) " +
                                    " VALUES (?, ?, ?, ?)");
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
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE coworking.reservation SET user_id = ?, start_date = ?, " +
                            "end_date = ?, reservation_place_id = ? WHERE id = ?");
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
            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM coworking.reservation WHERE id = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }
}
