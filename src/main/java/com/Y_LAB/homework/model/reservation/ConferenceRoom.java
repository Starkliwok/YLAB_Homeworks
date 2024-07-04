package com.Y_LAB.homework.model.reservation;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Класс конференц-зала наследуется от {@link ReservationPlace}, используется для бронирования
 * @author Денис Попов
 * @version 2.0
 */
@NoArgsConstructor
public class ConferenceRoom extends ReservationPlace {

    public ConferenceRoom(int id, String name, double placeArea, double costPerHour, int numberOfSeats) {
        super(id, name, placeArea, costPerHour, numberOfSeats);
    }

    public ConferenceRoom(String name, double placeArea, double costPerHour, int numberOfSeats) {
        super(name, placeArea, costPerHour, numberOfSeats);
    }
}
