package com.Y_LAB.homework.aop.dao.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLAuditConstants {

    /**Константа SQL запроса для создания служебной схемы*/
    public static final String CREATE_COWORKING_SERVICE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS coworking_service";

    /**Константа SQL запроса для получения всех аудитов отсортированных по id*/
    public static final String AUDIT_GET_ALL_ORDER_BY_ID = "SELECT * FROM coworking.user_audit ORDER BY id";

    /**Константа SQL запроса для получения аудита по id*/
    public static final String AUDIT_FIND_BY_ID = "SELECT user_id, date, class_name, method_name, audit_result FROM coworking.user_audit WHERE id = ?";

    /**Константа SQL запроса для добавления новой записи аудита*/
    public static final String AUDIT_FULL_INSERT = "INSERT INTO coworking.user_audit (user_id, date, class_name, method_name, audit_result) VALUES (?, ?, ?, ?, ?)";

}
