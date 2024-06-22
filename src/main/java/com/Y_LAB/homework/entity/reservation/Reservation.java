package com.Y_LAB.homework.entity.reservation;

import com.Y_LAB.homework.entity.roles.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Класс описывает бронь помещений
 * @author Денис Попов
 * @version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
public class Reservation {

    /** Статическое поле количества мест для бронирования, используется для инициализации поля {@link Reservation#id}.*/
    @EqualsAndHashCode.Exclude
    private static int reservationsCount;

    /** Поле уникального идентификатора, которое задается при создании объекта, это поле невозможно изменить.*/
    @EqualsAndHashCode.Exclude
    private final int id;

    /** Поле пользователя, владельца брони.*/
    private User user;

    /** Поле даты начала брони.*/
    private Date startDate;

    /** Поле даты окончания брони.*/
    private Date endDate;

    /** Поле места бронирования.*/
    private ReservationPlace reservationPlace;

    /** Конструктор, инициализирует поле {@link Reservation#id}, которое задается по количеству всех броней на данный момент*/
    public Reservation() {
        this.id = ++reservationsCount;
    }

    @Override
    public String toString() {
        return "\nБронь" +
                "\nИдентификатор пользователя = " + user.getId() +
                "\nДата начала = " +
                startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) +
                "\nДата окончания = " +
                endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + reservationPlace.toString();
    }
}
