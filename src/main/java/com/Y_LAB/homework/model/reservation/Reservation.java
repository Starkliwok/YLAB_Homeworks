package com.Y_LAB.homework.model.reservation;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Класс описывает бронь помещений
 * @author Денис Попов
 * @version 2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}
