package com.Y_LAB.homework.in.controller.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы именований эндпоинтов
 * @author Денис Попов
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerPathConstants {

    public static final String CONTROLLER_ADMIN_PATH = "/admin";

    public static final String CONTROLLER_USER_PATH = "/user";

    public static final String CONTROLLER_AUDIT_PATH = "/audit";

    public static final String CONTROLLER_SORT_PATH = "/sort";

    public static final String CONTROLLER_LOGIN_PATH = "/login";

    public static final String CONTROLLER_LOGOUT_PATH = "/logout";

    public static final String CONTROLLER_REGISTRATION_PATH = "/registration";

    public static final String CONTROLLER_RESERVATION_PATH = "/reservation";

    public static final String CONTROLLER_DATE_SLOT_PATH = "/date_slot";

    public static final String CONTROLLER_TIME_SLOT_PATH = "/time_slot";

    public static final String CONTROLLER_RESERVATION_PLACE_PATH = "/reservation_place";

}
