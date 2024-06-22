package com.Y_LAB.homework.entity.reservation;

/**
 * Класс конференц-зала наследуется от {@link ReservationPlace}, используется для бронирования
 * @author Денис Попов
 * @version 1.0
 */
public class ConferenceRoom extends ReservationPlace {
    public ConferenceRoom(String name, double placeArea, double costPerDayInDollars, int numberOfSeats) {
        super(name, placeArea, costPerDayInDollars, numberOfSeats);
    }

    public ConferenceRoom() {
        super();
    }

    @Override
    public String toString() {
        return "\nКонференц зал " +
                "\nНазвание = '" + this.getName() + '\'' +
                "\nПлощадь в метрах = " + this.getPlaceArea() +
                "\nСтоимость аренды за сутки = " + this.getCostPerDayInDollars() + "$" +
                "\nКоличество сидений = " + this.getNumberOfSeats();
    }
}
