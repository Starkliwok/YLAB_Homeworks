package com.Y_LAB.homework.model.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReservationPlaceRequestDTO {

    private int typeId;

    private String name;

    private double placeArea;

    private double costPerHour;

    private int numberOfSeats;
}