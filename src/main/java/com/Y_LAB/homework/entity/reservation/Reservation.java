package com.Y_LAB.homework.entity.reservation;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс описывает бронь помещений
 * @author Денис Попов
 * @version 2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    /** Поле уникального идентификатора, которое задается при создании объекта, это поле невозможно изменить.*/
    private long id;

    /** Поле пользователя, владельца брони.*/
    private long userId;

    /** Поле даты начала брони.*/
    private LocalDateTime startDate;

    /** Поле даты окончания брони.*/
    private LocalDateTime endDate;

    /** Поле места бронирования.*/
    private ReservationPlace reservationPlace;

    @Override
    public String toString() {
        return "\nБронь" +
                "\nИдентификатор пользователя = " + userId +
                "\nДата начала = " +
                startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) +
                "\nДата окончания = " +
                endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) +
                reservationPlace.toString();
    }
}
