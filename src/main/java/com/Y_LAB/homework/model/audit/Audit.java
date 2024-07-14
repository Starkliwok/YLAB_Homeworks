package com.Y_LAB.homework.model.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс аудита пользователя
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@Builder
public class Audit {

    /** Поле уникального идентификатора аудита*/
    private long id;

    /** Поле уникального идентификатора пользователя*/
    private Long userId;

    /** Поле даты и времени записи аудита*/
    private LocalDateTime localDateTime;

    /** Класс в котором произошло событие*/
    private String className;

    /** Метод в котором произошло событие*/
    private String methodName;

    /** Результат события*/
    private AuditResult result;

    public Audit(Long userId, String className, String methodName, AuditResult result) {
        this.userId = userId;
        this.localDateTime = LocalDateTime.now();
        this.className = className;
        this.methodName = methodName;
        this.result = result;
    }
}
