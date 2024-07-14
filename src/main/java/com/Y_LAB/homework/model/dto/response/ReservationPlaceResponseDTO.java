package com.Y_LAB.homework.model.dto.response;

import lombok.Data;

/**
 * DTO Response объекта места для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@Data
public class ReservationPlaceResponseDTO {

    private int id;

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}
