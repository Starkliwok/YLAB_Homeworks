package com.Y_LAB.homework.model.dto.request;

import lombok.Data;

@Data
public class ReservationPlaceFullRequestDTO {

    private int id;

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}
