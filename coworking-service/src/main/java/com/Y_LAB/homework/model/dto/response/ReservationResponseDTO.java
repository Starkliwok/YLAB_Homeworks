package com.Y_LAB.homework.model.dto.response;

import com.Y_LAB.homework.constants.DateTimePatternConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Response объекта брони
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {

    private long id;

    private long userId;

    @JsonFormat(pattern = DateTimePatternConstants.DATE_HOUR_PATTERN)
    private LocalDateTime startDate;

    @JsonFormat(pattern = DateTimePatternConstants.DATE_HOUR_PATTERN)
    private LocalDateTime endDate;

    private int reservationPlaceId;
}
