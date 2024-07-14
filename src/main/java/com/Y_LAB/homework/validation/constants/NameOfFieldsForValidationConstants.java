package com.Y_LAB.homework.validation.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы для описания названий полей объектов
 * @author Денис Попов
 * @version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameOfFieldsForValidationConstants {
    public static final String FIELD_USERNAME = "username";

    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_START_DATE = "startDate";

    public static final String FIELD_END_DATE = "endDate";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_RESERVATION_PLACE_ID = "reservationPlaceId";

    public static final String FIELD_TYPE_ID = "typeId";

    public static final String FIELD_PLACE_AREA = "placeArea";

    public static final String FIELD_COST_PER_HOUR = "costPerHour";

    public static final String FIELD_NUMBER_OF_SEATS = "numberOfSeats";
}
