package com.Y_LAB.homework.entity.reservation;

import lombok.NoArgsConstructor;

/**
 * Класс рабочего места наследуется от {@link ReservationPlace}, используется для бронирования
 * @author Денис Попов
 * @version 2.0
 */
@NoArgsConstructor
public class Workplace extends ReservationPlace {

    public Workplace(int id, String name, double placeArea, double costPerDayInDollars, int numberOfSeats) {
        super(id, name, placeArea, costPerDayInDollars, numberOfSeats);
    }

    public Workplace(String name, double placeArea, double costPerDayInDollars, int numberOfSeats) {
        super(name, placeArea, costPerDayInDollars, numberOfSeats);
    }

    @Override
    public String toString() {
        return "\nРабочее место " +
                "\nНазвание = '" + this.getName() + '\'' +
                "\nПлощадь в метрах = " + this.getPlaceArea() +
                "\nСтоимость аренды за сутки = " + this.getCostPerHour() + "$" +
                "\nКоличество сидений = " + this.getNumberOfSeats();
    }
}
