package com.Y_LAB.homework.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO Request объекта места для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPlaceRequestDTO {

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}
