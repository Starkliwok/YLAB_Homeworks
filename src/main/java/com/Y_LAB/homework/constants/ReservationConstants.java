package com.Y_LAB.homework.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы для создания бронирований
 * @author Денис Попов
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReservationConstants {

    /** Константа количества часов начала бронирования мест*/
    public static final int START_HOUR_FOR_RESERVATION = 8;

    /** Константа количества часов окончания бронирования мест*/
    public static final int END_HOUR_FOR_RESERVATION = 22;

    /** Константа максимального количества дней для бронирования мест с даты создания бронирования*/
    public static final int RESERVATION_PERIOD = 6;
}
