package com.Y_LAB.homework.model.dto.response;

import com.Y_LAB.homework.model.audit.AuditResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_TIME_PATTERN;

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

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime localDateTime;

    private String className;

    private String methodName;

    private AuditResult result;
}
