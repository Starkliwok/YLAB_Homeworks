package com.Y_LAB.homework.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_PATTERN;

/**
 * Request объект содержащий дату
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateRequest {

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate localDate;
}
