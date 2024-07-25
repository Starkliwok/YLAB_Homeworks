package com.Y_LAB.homework.dao.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит константы SQL запросов для DAO слоя
 * @author Денис Попов
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLConstants {

    /**Константа SQL запроса для получения всех броней*/
    public static final String RESERVATION_GET_ALL = "SELECT * FROM coworking.reservation";

    /**Константа SQL запроса для получения всех броней отсортированных по id пользователя*/
    public static final String RESERVATION_GET_ALL_ORDER_BY_USER_ID = "SELECT * FROM coworking.reservation ORDER BY user_id";

    /**Константа SQL запроса для получения всех броней отсортированных по дате начала бронирования*/
    public static final String RESERVATION_GET_ALL_ORDER_BY_START_DATE = "SELECT * FROM coworking.reservation ORDER BY start_date";

    /**Константа SQL запроса для получения всех броней по id места для бронирования*/
    public static final String RESERVATION_FIND_BY_RESERVATION_PLACE_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE reservation_place_id = ?";

    /**Константа SQL запроса для получения всех броней по id пользователя*/
    public static final String RESERVATION_FIND_BY_USER_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE user_id = ?";

    /**Константа SQL запроса для получения брони по id*/
    public static final String RESERVATION_FIND_BY_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE id = ?";

    /**Константа SQL запроса для получения брони по всем полям кроме id*/
    public static final String RESERVATION_FIND_BY_FIELDS_WITHOUT_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE user_id = ? AND reservation_place_id = ? AND start_date = ? AND end_date = ?";

    /**Константа SQL запроса для добавления новой брони*/
    public static final String RESERVATION_FULL_INSERT = "INSERT INTO coworking.reservation (user_id, start_date, end_date, reservation_place_id) VALUES (?, ?, ?, ?)";

    /**Константа SQL запроса для обновления брони*/
    public static final String RESERVATION_FULL_UPDATE = "UPDATE coworking.reservation SET user_id = ?, start_date = ?, end_date = ?, reservation_place_id = ? WHERE id = ?";

    /**Константа SQL запроса для удаления брони по id*/
    public static final String RESERVATION_DELETE_BY_ID = "DELETE FROM coworking.reservation WHERE id = ?";

    /**Константа SQL запроса для получения всех мест для бронирования отсортированных по id*/
    public static final String RESERVATION_PLACE_GET_ALL_ORDER_BY_ID = "SELECT * FROM coworking.reservation_place ORDER BY id";

    /**Константа SQL запроса для получения всех мест для бронирования по id типа места*/
    public static final String RESERVATION_PLACE_FIND_BY_RESERVATION_TYPE_ID = "SELECT id, name, place_area, cost_per_hour, number_of_seats FROM coworking.reservation_place WHERE reservation_type_id = ?";

    /**Константа SQL запроса для получения места для бронирования по id*/
    public static final String RESERVATION_PLACE_FIND_BY_ID = "SELECT * FROM coworking.reservation_place WHERE id = ?";

    /**Константа SQL запроса для добавления нового места для бронирования*/
    public static final String RESERVATION_PLACE_FULL_INSERT = "INSERT INTO coworking.reservation_place (reservation_type_id, name, place_area, cost_per_hour, number_of_seats) VALUES (?, ?, ?, ?, ?)";

    /**Константа SQL запроса для обновления места для бронирования*/
    public static final String RESERVATION_PLACE_FULL_UPDATE_WHERE_ID = "UPDATE coworking.reservation_place SET reservation_type_id = ?, name = ?, place_area = ?, cost_per_hour = ?, number_of_seats = ? WHERE id = ?";

    /**Константа SQL запроса для удаления места для бронирования по id*/
    public static final String RESERVATION_PLACE_DELETE_BY_ID = "DELETE FROM coworking.reservation_place WHERE id = ?";

    /**Константа SQL запроса для получения всех пользователей*/
    public static final String USER_GET_ALL = "SELECT * FROM coworking.user";

    /**Константа SQL запроса для получения пользователя по логину и паролю*/
    public static final String USER_GET_ID_WHERE_FIELDS = "SELECT id FROM coworking.user WHERE name = ? AND password = ?";

    /**Константа SQL запроса для получения id пользователя по логину*/
    public static final String USER_GET_ID_WHERE_NAME = "SELECT id FROM coworking.user WHERE name = ?";

    /**Константа SQL запроса для получения пользователя по id*/
    public static final String USER_GET_BY_ID = "SELECT name, password FROM coworking.user WHERE id = ?";

    /**Константа SQL запроса для получения пользователя по логину*/
    public static final String USER_GET_BY_NAME = "SELECT * FROM coworking.user WHERE name = ?";

    /**Константа SQL запроса для сохранения нового пользователя*/
    public static final String USER_FULL_INSERT = "INSERT INTO coworking.user (name, password) VALUES (?, ?)";

    /**Константа SQL запроса для обновления пользователя*/
    public static final String USER_FULL_UPDATE_WHERE_ID = "UPDATE coworking.user SET name = ?, password = ? WHERE id = ?";

    /**Константа SQL запроса для удаления пользователя по id*/
    public static final String USER_DELETE_WHERE_ID = "DELETE FROM coworking.user WHERE id = ?";

    /**Константа SQL запроса для получения администратора по id пользователя*/
    public static final String USER_FIND_IN_ADMIN_TABLE_WHERE_ID = "SELECT * FROM coworking.admin WHERE user_id = ?";
}
