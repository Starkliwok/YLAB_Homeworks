package com.Y_LAB.homework.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_TIME_WITHOUT_SECONDS_PATTERN;

@Data
public class ReservationPutRequestDTO {

    private long id;

    @JsonFormat(pattern = DATE_TIME_WITHOUT_SECONDS_PATTERN)
    private LocalDateTime startDate;

    @JsonFormat(pattern = DATE_TIME_WITHOUT_SECONDS_PATTERN)
    private LocalDateTime endDate;

    private int reservationPlaceId;
}
