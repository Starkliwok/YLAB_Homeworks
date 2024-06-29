package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.entity.reservation.ConferenceRoom;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.*;

/**
 * Класс ДАО слоя для взаимодействия с местами для бронирований
 * @author Денис Попов
 * @version 2.0
 */
@AllArgsConstructor
public class ReservationPlaceDAOImpl implements ReservationPlaceDAO {

    /** Поле для подключения к базе данных*/
    private final Connection connection;

    public ReservationPlaceDAOImpl() {
        connection = ConnectionToDatabase.getConnection();
    }

    /** {@inheritDoc}*/
    @Override
    public List<ReservationPlace> getAllReservationPlaces() {
        List<ReservationPlace> reservationPlaces = new ArrayList<>();
        ReservationPlace reservationPlace;
        try {
            Statement statement = connection.createStatement();
            ResultSet reservationPlaceResultSet = statement.executeQuery("SELECT * FROM coworking.reservation_place");
            while (reservationPlaceResultSet.next()) {
                reservationPlace = getReservationPlaceFromResultSet(reservationPlaceResultSet);
                reservationPlaces.add(reservationPlace);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservationPlaces;
    }

    /** {@inheritDoc}*/
    @Override
    public List<ReservationPlace> getAllReservationPlacesByTypes(ReservationPlace reservationPlace) {
        List<ReservationPlace> reservationPlaces = new ArrayList<>();
        ReservationPlace reservationPlace2;
        int typeId = reservationPlace instanceof ConferenceRoom ? 1 : 2;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, name, place_area, cost_per_hour, number_of_seats FROM coworking.reservation_place " +
                            "WHERE reservation_type_id = ?");
            preparedStatement.setInt(1, typeId);
            preparedStatement.execute();
            ResultSet reservationPlaceResultSet = preparedStatement.getResultSet();
            while (reservationPlaceResultSet.next()) {
                int id = reservationPlaceResultSet.getInt(1);
                String name = reservationPlaceResultSet.getString(2);
                double placeArea = reservationPlaceResultSet.getDouble(3);
                double costPerHour = reservationPlaceResultSet.getDouble(4);
                int numberOfSeats = reservationPlaceResultSet.getInt(5);
                if(reservationPlace instanceof ConferenceRoom) {
                    reservationPlace2 = new ConferenceRoom(id, name, placeArea, costPerHour, numberOfSeats);
                    reservationPlaces.add(reservationPlace2);
                } else if(reservationPlace instanceof Workplace) {
                    reservationPlace2 = new Workplace(id, name, placeArea, costPerHour, numberOfSeats);
                    reservationPlaces.add(reservationPlace2);
                }
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservationPlaces;
    }

    /**{@inheritDoc}*/
    @Override
    public ReservationPlace getReservationPlace(int id) {
       ReservationPlace reservationPlace = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM coworking.reservation_place WHERE id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            ResultSet reservationPlaceResultSet = preparedStatement.getResultSet();
            if(reservationPlaceResultSet.next()) {
                reservationPlace = getReservationPlaceFromResultSet(reservationPlaceResultSet);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return reservationPlace;
    }

    /**
     * Метод для получения объекта места для брони из {@link ResultSet}
     * @param reservationPlaceResultSet объект {@link ResultSet}
     * @throws SQLException Если есть проблемы при получении значений из колонок объекта {@link ResultSet}
     * @return объект места для бронирования
     */
    private ReservationPlace getReservationPlaceFromResultSet(ResultSet reservationPlaceResultSet) throws SQLException {
        int id = reservationPlaceResultSet.getInt(1);
        int typeId = reservationPlaceResultSet.getInt(2);
        String name = reservationPlaceResultSet.getString(3);
        double placeArea = reservationPlaceResultSet.getDouble(4);
        double costPerHour = reservationPlaceResultSet.getDouble(5);
        int numberOfSeats = reservationPlaceResultSet.getInt(6);
        if(typeId == 1) {
            return new ConferenceRoom(id, name, placeArea, costPerHour, numberOfSeats);
        } else if(typeId == 2) {
            return new Workplace(id, name, placeArea, costPerHour, numberOfSeats);
        }
        return null;
    }

    /**{@inheritDoc}*/
    @Override
    public void saveReservationPlace(ReservationPlace reservationPlace) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "INSERT INTO coworking.reservation_place (reservation_type_id, name, place_area, " +
                                    "cost_per_hour, number_of_seats) VALUES (?, ?, ?, ?, ?)");
            setReservationPlaceToPreparedStatement(reservationPlace, preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void updateReservationPlace(ReservationPlace reservationPlace) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE coworking.reservation_place SET reservation_type_id = ?, " +
                            "name = ?, place_area = ?, cost_per_hour = ?, number_of_seats = ? WHERE id = ?");
            setReservationPlaceToPreparedStatement(reservationPlace, preparedStatement);
            preparedStatement.setInt(6, reservationPlace.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /**
     * Метод для добавления объекта места для бронирования в {@link PreparedStatement}
     * @param reservationPlace объект места для бронирования
     * @param preparedStatement объект {@link PreparedStatement}
     * @throws SQLException Если есть проблемы при установке значений в объект {@link PreparedStatement}
     */
    private void setReservationPlaceToPreparedStatement
            (ReservationPlace reservationPlace, PreparedStatement preparedStatement) throws SQLException {
        int typeId = reservationPlace instanceof ConferenceRoom ? 1 : 2;
        preparedStatement.setInt(1, typeId);
        preparedStatement.setString(2, reservationPlace.getName());
        preparedStatement.setDouble(3, reservationPlace.getPlaceArea());
        preparedStatement.setDouble(4, reservationPlace.getCostPerHour());
        preparedStatement.setInt(5, reservationPlace.getNumberOfSeats());
    }

    /**{@inheritDoc}*/
    @Override
    public void deleteReservationPlace(int id) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM coworking.reservation_place WHERE id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }
}
