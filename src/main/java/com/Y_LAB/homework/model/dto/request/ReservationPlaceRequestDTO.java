package com.Y_LAB.homework.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.*;

/**
 * DTO Request объекта места для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPlaceRequestDTO {

    @Size(min = TYPE_ID_MIN_LENGTH, max = TYPE_ID_MAX_LENGTH)
    private int typeId;

    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}
