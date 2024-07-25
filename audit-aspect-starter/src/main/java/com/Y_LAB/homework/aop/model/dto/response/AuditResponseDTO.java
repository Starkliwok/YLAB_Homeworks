package com.Y_LAB.homework.aop.model.dto.response;

import com.Y_LAB.homework.aop.model.audit.AuditResult;
import com.Y_LAB.homework.aop.model.constants.DateTimePatternConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * DTO Response объекта аудита
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditResponseDTO {

    private long id;

    private Long userId;

    @JsonFormat(pattern = DateTimePatternConstants.DATE_TIME_PATTERN)
    private LocalDateTime localDateTime;

    private String className;

    private String methodName;

    private AuditResult result;
}
