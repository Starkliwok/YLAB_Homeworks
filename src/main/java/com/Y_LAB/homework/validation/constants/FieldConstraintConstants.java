package com.Y_LAB.homework.validation.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы для описания границ полей объектов
 * @author Денис Попов
 * @version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldConstraintConstants {
    /** Константа минимальной длины логина пользователя*/
    public static final int LOGIN_MIN_LENGTH = 2;

    /** Константа максимальной длины логина пользователя*/
    public static final int LOGIN_MAX_LENGTH = 30;

    /** Константа минимальной длины пароля пользователя*/
    public static final int PASSWORD_MIN_LENGTH = 4;

    /** Константа максимальной длины пароля пользователя*/
    public static final int PASSWORD_MAX_LENGTH = 30;

    public static final int NAME_MIN_LENGTH = 2;

    public static final int NAME_MAX_LENGTH = 30;

    public static final int TYPE_ID_MIN_LENGTH = 1;

    public static final int TYPE_ID_MAX_LENGTH = 2;
}
