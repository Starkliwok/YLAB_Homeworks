package com.Y_LAB.homework.entity.reservation;

import lombok.NoArgsConstructor;

/**
 * Класс конференц-зала наследуется от {@link ReservationPlace}, используется для бронирования
 * @author Денис Попов
 * @version 2.0
 */
@NoArgsConstructor
public class ConferenceRoom extends ReservationPlace {

    public ConferenceRoom(int id, String name, double placeArea, double costPerDayInDollars, int numberOfSeats) {
        super(id, name, placeArea, costPerDayInDollars, numberOfSeats);
    }

    public ConferenceRoom(String name, double placeArea, double costPerDayInDollars, int numberOfSeats) {
        super(name, placeArea, costPerDayInDollars, numberOfSeats);
    }

    @Override
    public String toString() {
        return "\nКонференц зал " +
                "\nНазвание = '" + this.getName() + '\'' +
                "\nПлощадь в метрах = " + this.getPlaceArea() +
                "\nСтоимость аренды за сутки = " + this.getCostPerHour() + "$" +
                "\nКоличество сидений = " + this.getNumberOfSeats();
    }
}
