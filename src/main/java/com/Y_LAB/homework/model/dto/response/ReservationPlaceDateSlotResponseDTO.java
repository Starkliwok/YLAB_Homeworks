package com.Y_LAB.homework.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPlaceDateSlotResponseDTO {
    private int id;

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;

    @JsonFormat(pattern = DATE_PATTERN)
    private List<LocalDate> availableDates;
}
