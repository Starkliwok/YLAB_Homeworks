package com.Y_LAB.homework.dao.constants;

public class SQLConstants {
    public static final String AUDIT_GET_ALL_ORDER_BY_ID = "SELECT id, user_id, date, action FROM coworking.user_audit ORDER BY id";
    public static final String AUDIT_FIND_BY_ID = "SELECT user_id, date, action FROM coworking.user_audit WHERE id = ?";
    public static final String AUDIT_FULL_INSERT = "INSERT INTO coworking.user_audit (user_id, date, action) VALUES (?, ?, ?)";
    public static final String RESERVATION_GET_ALL = "SELECT * FROM coworking.reservation";
    public static final String RESERVATION_GET_ALL_ORDER_BY_USER_ID = "SELECT * FROM coworking.reservation ORDER BY user_id";
    public static final String RESERVATION_GET_ALL_ORDER_BY_START_DATE = "SELECT * FROM coworking.reservation ORDER BY start_date";
    public static final String RESERVATION_FIND_BY_RESERVATION_PLACE_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE reservation_place_id = ?";
    public static final String RESERVATION_FIND_BY_USER_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE user_id = ?";
    public static final String RESERVATION_FIND_BY_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE id = ?";
    public static final String RESERVATION_FIND_BY_FIELDS_WITHOUT_ID = "SELECT id, user_id, reservation_place_id, start_date, end_date FROM coworking.reservation WHERE user_id = ? AND reservation_place_id = ? AND start_date = ? AND end_date = ?";
    public static final String RESERVATION_FULL_INSERT = "INSERT INTO coworking.reservation (user_id, start_date, end_date, reservation_place_id) VALUES (?, ?, ?, ?)";
    public static final String RESERVATION_FULL_UPDATE = "UPDATE coworking.reservation SET user_id = ?, start_date = ?, end_date = ?, reservation_place_id = ? WHERE id = ?";
    public static final String RESERVATION_DELETE_BY_ID = "DELETE FROM coworking.reservation WHERE id = ?";
    public static final String RESERVATION_PLACE_GET_ALL_ORDER_BY_ID = "SELECT * FROM coworking.reservation_place ORDER BY id";
    public static final String RESERVATION_PLACE_FIND_BY_RESERVATION_TYPE_ID = "SELECT id, name, place_area, cost_per_hour, number_of_seats FROM coworking.reservation_place WHERE reservation_type_id = ?";
    public static final String RESERVATION_PLACE_FIND_BY_ID = "SELECT * FROM coworking.reservation_place WHERE id = ?";
    public static final String RESERVATION_PLACE_FULL_INSERT = "INSERT INTO coworking.reservation_place (reservation_type_id, name, place_area, cost_per_hour, number_of_seats) VALUES (?, ?, ?, ?, ?)";
    public static final String RESERVATION_PLACE_FULL_UPDATE_WHERE_ID = "UPDATE coworking.reservation_place SET reservation_type_id = ?, name = ?, place_area = ?, cost_per_hour = ?, number_of_seats = ? WHERE id = ?";
    public static final String RESERVATION_PLACE_DELETE_BY_ID = "DELETE FROM coworking.reservation_place WHERE id = ?";
    public static final String USER_GET_ALL = "SELECT * FROM coworking.user";
    public static final String USER_GET_ID_WHERE_FIELDS = "SELECT id FROM coworking.user WHERE name = ? AND password = ?";
    public static final String USER_GET_ID_WHERE_NAME = "SELECT id FROM coworking.user WHERE name = ?";
    public static final String USER_GET_BY_ID = "SELECT name, password FROM coworking.user WHERE id = ?";
    public static final String USER_GET_BY_NAME = "SELECT * FROM coworking.user WHERE name = ?";
    public static final String USER_FULL_INSERT = "INSERT INTO coworking.user (name, password) VALUES (?, ?)";
    public static final String USER_FULL_UPDATE_WHERE_ID = "UPDATE coworking.user SET name = ?, password = ? WHERE id = ?";
    public static final String USER_DELETE_WHERE_ID = "DELETE FROM coworking.user WHERE id = ?";
    public static final String USER_FIND_IN_ADMIN_TABLE_WHERE_ID = "SELECT * FROM coworking.admin WHERE user_id = ?";

}
