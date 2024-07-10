package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.request.ReservationPlaceFullRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationPlaceMapper {

    @Mapping(target = "typeId", expression = "java(typeId(reservationPlace))")
    ReservationPlaceResponseDTO toResponseDTO(ReservationPlace reservationPlace);

    @Mapping(target = "typeId", expression = "java(typeId(reservationPlace))")
    ReservationPlaceDateSlotResponseDTO toFreeSlotResponseDTO(ReservationPlace reservationPlace, List<LocalDate> availableDates);
    ConferenceRoom toConferenceRoom(ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO);

    Workplace toWorkplace(ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO);

    List<ReservationPlaceResponseDTO> toResponseDTOList(List<ReservationPlace> reservationPlaceList);
    default int typeId(ReservationPlace reservationPlace) {
        if(reservationPlace instanceof ConferenceRoom) {
            return 1;
        } else if (reservationPlace instanceof Workplace) {
            return 2;
        }
        return 0;
    }
}