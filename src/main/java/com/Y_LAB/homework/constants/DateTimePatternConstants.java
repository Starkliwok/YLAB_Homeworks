package com.Y_LAB.homework.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы для дат
 * @author Денис Попов
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimePatternConstants {

    /**Константа даты в формате dd-MM-yyyy*/
    public static final String DATE_PATTERN = "dd-MM-yyyy";

    /**Константа даты и времени в формате dd-MM-yyyy HH:mm:ss*/
    public static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";

    /**Константа даты и времени в формате dd-MM-yyyy HH*/
    public static final String DATE_HOUR_PATTERN = "dd-MM-yyyy HH";
}
