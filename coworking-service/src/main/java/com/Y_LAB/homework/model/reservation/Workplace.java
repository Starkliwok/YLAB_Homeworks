package com.Y_LAB.homework.model.reservation;

import lombok.NoArgsConstructor;

/**
 * Класс рабочего места наследуется от {@link ReservationPlace}, используется для бронирования
 * @author Денис Попов
 * @version 2.0
 */
@NoArgsConstructor
public class Workplace extends ReservationPlace {

    public Workplace(int id, String name, double placeArea, double costPerHour, int numberOfSeats) {
        super(id, name, placeArea, costPerHour, numberOfSeats);
    }

    public Workplace(String name, double placeArea, double costPerHour, int numberOfSeats) {
        super(name, placeArea, costPerHour, numberOfSeats);
    }
}
