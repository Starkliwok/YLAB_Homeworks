package com.Y_LAB.homework.model.dto.response;

import lombok.Data;

@Data
public class ReservationPlaceResponseDTO {

    private int id;

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}
