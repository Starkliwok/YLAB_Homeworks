package com.Y_LAB.homework.entity.reservation;

/**
 * Класс рабочего места наследуется от {@link ReservationPlace}, используется для бронирования
 * @author Денис Попов
 * @version 1.0
 */
public class Workplace extends ReservationPlace {

    public Workplace(String name, double placeArea, double costPerDayInDollars) {
        super(name, placeArea, costPerDayInDollars, 1);
    }

    public Workplace() {
        super();
    }

    @Override
    public String toString() {
        return "\nРабочее место " +
                "\nНазвание = '" + this.getName() + '\'' +
                "\nПлощадь в метрах = " + this.getPlaceArea() +
                "\nСтоимость аренды за сутки = " + this.getCostPerDayInDollars() + "$" +
                "\nКоличество сидений = " + this.getNumberOfSeats();
    }
}
