package com.Y_LAB.homework.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateRequest {

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate localDate;
}
