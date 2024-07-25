package com.Y_LAB.homework.model.request;

import com.Y_LAB.homework.constants.DateTimePatternConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request объект содержащий дату
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateRequest {

    @JsonFormat(pattern = DateTimePatternConstants.DATE_PATTERN)
    private LocalDate localDate;
}
