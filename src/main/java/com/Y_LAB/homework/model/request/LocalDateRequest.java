package com.Y_LAB.homework.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_PATTERN;

@Data
public class LocalDateRequest {

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate localDate;
}
