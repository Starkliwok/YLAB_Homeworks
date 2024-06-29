package com.Y_LAB.homework.entity.reservation;

import lombok.*;


/**
 * Абстрактный класс места для бронирования используется для описания различных типов мест для бронирования
 * @author Денис Попов
 * @version 2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ReservationPlace {

    /** Поле уникального идентификатора, которое задается при создании объекта, это поле невозможно изменить.*/
    private int id;

    /** Поле названия места*/
    private String name;

    /** Поле площади места.*/
    private double placeArea;

    /** Поле стоимости аренды в долларах за сутки.*/
    private double costPerHour;

    /** Поле количества сидений.*/
    private int numberOfSeats;

    public ReservationPlace(String name, double placeArea, double costPerHour, int numberOfSeats) {
        this.name = name;
        this.placeArea = placeArea;
        this.costPerHour = costPerHour;
        this.numberOfSeats = numberOfSeats;
    }
}
