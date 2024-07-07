package com.Y_LAB.homework.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_TIME_PATTERN;

@Data
public class AuditResponseDTO {

    private long id;

    private Long userId;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime localDateTime;

    private String action;
}
