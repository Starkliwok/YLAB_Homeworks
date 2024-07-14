package com.Y_LAB.homework.constants;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы для типов мест для бронирования помещения
 * @author Денис Попов
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReservationPlaceConstants {

    /** Константа конференц-зала, одного из типов мест для бронирования*/
    public static final String RESERVATION_PLACE_CONFERENCE_ROOM_TYPE = "Conference room";

    /** Константа рабочего места, одного из типов мест для бронирования*/
    public static final String RESERVATION_PLACE_WORKPLACE_TYPE = "Workplace";
}
