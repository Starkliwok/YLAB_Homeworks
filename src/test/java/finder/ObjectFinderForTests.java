package finder;

import com.Y_LAB.homework.entity.reservation.ConferenceRoom;
import com.Y_LAB.homework.entity.reservation.Reservation;
import com.Y_LAB.homework.entity.reservation.ReservationPlace;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class ObjectFinderForTests {
    @SneakyThrows
    public static void setReservationIdFromDB(Reservation reservation, Connection connection) {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM coworking.reservation " +
                "WHERE user_id = ? AND reservation_place_id = ? AND start_date = ? AND end_date = ?");
        preparedStatement.setLong(1, reservation.getUserId());
        preparedStatement.setInt(2, reservation.getReservationPlace().getId());
        preparedStatement.setTimestamp(3, Timestamp.valueOf(reservation.getStartDate()));
        preparedStatement.setTimestamp(4, Timestamp.valueOf(reservation.getEndDate()));
        preparedStatement.execute();
        ResultSet reservationResultSet = preparedStatement.getResultSet();
        if(reservationResultSet.next()) {
            long id = reservationResultSet.getLong(1);
            reservation.setId(id);
        }
    }

    @SneakyThrows
    public static void setReservationPlaceIdFromDB(ReservationPlace reservationPlace, Connection connection) {
        int typeId = reservationPlace instanceof ConferenceRoom ? 1 : 2;
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM coworking.reservation_place " +
                        "WHERE reservation_type_id = ? AND name = ? AND place_area = ? AND cost_per_hour = ? " +
                        "AND number_of_seats = ?");
        preparedStatement.setInt(1, typeId);
        preparedStatement.setString(2, reservationPlace.getName());
        preparedStatement.setDouble(3, reservationPlace.getPlaceArea());
        preparedStatement.setDouble(4, reservationPlace.getCostPerHour());
        preparedStatement.setInt(5, reservationPlace.getNumberOfSeats());
        preparedStatement.execute();
        ResultSet reservationPlaceResultSet = preparedStatement.getResultSet();
        if (reservationPlaceResultSet.next()) {
            int id = reservationPlaceResultSet.getInt(1);
            reservationPlace.setId(id);
        }
    }
}
