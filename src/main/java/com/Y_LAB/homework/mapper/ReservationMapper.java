package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ReservationMapper {

    @Mapping(source = "reservation.reservationPlace.id", target = "reservationPlaceId")
    ReservationResponseDTO toResponseDTO(Reservation reservation);

    List<ReservationResponseDTO> toResponseDTOList(List<Reservation> reservations);

}