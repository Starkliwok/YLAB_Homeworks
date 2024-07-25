package com.Y_LAB.homework.model.dto.request;

import com.Y_LAB.homework.constants.DateTimePatternConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Request объекта брони
 * @author Денис Попов
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDTO {

    @JsonFormat(pattern = DateTimePatternConstants.DATE_HOUR_PATTERN)
    private LocalDateTime startDate;

    @JsonFormat(pattern = DateTimePatternConstants.DATE_HOUR_PATTERN)
    private LocalDateTime endDate;

    private int reservationPlaceId;
}
