package com.Y_LAB.homework.model.dto.request;

import lombok.Data;

@Data
public class ReservationPlaceRequestDTO {

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}
