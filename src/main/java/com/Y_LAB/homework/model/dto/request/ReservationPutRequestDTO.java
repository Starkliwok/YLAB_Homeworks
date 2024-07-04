package com.Y_LAB.homework.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_HOUR_PATTERN;

@Data
public class ReservationPutRequestDTO {

    private long id;

    @JsonFormat(pattern = DATE_HOUR_PATTERN)
    private LocalDateTime startDate;

    @JsonFormat(pattern = DATE_HOUR_PATTERN)
    private LocalDateTime endDate;

    private int reservationPlaceId;
}
