package com.Y_LAB.homework.entity.reservation;

import lombok.*;


/**
 * Абстрактный класс места для бронирования используется для описания различных типов мест для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class ReservationPlace {

    /** Статическое поле количества мест для бронирования, используется для инициализации поля {@link ReservationPlace#id}.*/
    @EqualsAndHashCode.Exclude
    private static int countOfReservationPlaces;

    /** Поле уникального идентификатора, которое задается при создании объекта, это поле невозможно изменить.*/
    @EqualsAndHashCode.Exclude
    private final int id;

    /** Поле названия места*/
    private String name;

    /** Поле площади места.*/
    private double placeArea;

    /** Поле стоимости аренды в долларах за сутки.*/
    private double costPerDayInDollars;

    /** Поле количества сидений.*/
    private int numberOfSeats;

    /**
     * Конструктор, который принимает параметры и инициализирует поля класса,
     * поле {@link ReservationPlace#id задается по количеству всех мест для бронирования на данный момент}
     * @param name название
     * @param placeArea площадь места
     * @param costPerDayInDollars стоимость аренды в долларах за день
     * @param numberOfSeats количество сидений
     */
    public ReservationPlace(String name, double placeArea, double costPerDayInDollars, int numberOfSeats) {
        this.id = ++countOfReservationPlaces;
        this.name = name;
        this.placeArea = placeArea;
        this.costPerDayInDollars = costPerDayInDollars;
        this.numberOfSeats = numberOfSeats;
    }

    /** Конструктор, инициализирует поле {@link ReservationPlace#id},
     *  которое задается по количеству всех броней на данный момент
     */
    public ReservationPlace() {
        this.id = ++countOfReservationPlaces;
    }
}
