package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для преобразования сущностей мест для бронирования в DTO объект и наоборот
 * @author Денис Попов
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface ReservationPlaceMapper {

    /**
     * Преобразует объект места для бронирования в DTO Response объект места для бронирования
     * @param reservationPlace Место для бронирования
     * @return DTO Response объект места для бронирования
     */
    @Mapping(target = "typeId", source = "reservationPlace", qualifiedByName = "getTypeId")
    ReservationPlaceResponseDTO toResponseDTO(ReservationPlace reservationPlace);

    /**
     * Преобразует объект места для бронирования и список дат в DTO Response объект места для бронирования
     * содержащий свободные даты для бронирования помещения
     * @param reservationPlace Место для бронирования
     * @param availableDates Список свободных дат
     * @return DTO Response объект места для бронирования содержащий свободные даты для бронирования помещения
     */
    @Mapping(target = "typeId", source = "reservationPlace", qualifiedByName = "getTypeId")
    ReservationPlaceDateSlotResponseDTO toFreeSlotResponseDTO(ReservationPlace reservationPlace, List<LocalDate> availableDates);

    /**
     * Преобразует DTO Request объект места для бронирования в объект конференц-зала
     * @param reservationPlaceRequestDTO DTO Request объект места для бронирования
     * @return Конференц-зал
     */
    ConferenceRoom toConferenceRoom(ReservationPlaceRequestDTO reservationPlaceRequestDTO);

    /**
     * Преобразует DTO Request объект места для бронирования в объект рабочего места
     * @param reservationPlaceRequestDTO DTO Request объект места для бронирования
     * @return Рабочее место
     */
    Workplace toWorkplace(ReservationPlaceRequestDTO reservationPlaceRequestDTO);

    /**
     * Преобразует список объектов мест для бронирования в список DTO Response объектов мест для бронирования
     * @param reservationPlaceList Список объектов мест для бронирования
     * @return Список DTO Response объектов мест для бронирования
     */
    List<ReservationPlaceResponseDTO> toResponseDTOList(List<ReservationPlace> reservationPlaceList);

    @Named("getTypeId")
    default int getTypeId(ReservationPlace reservationPlace) {
        if(reservationPlace instanceof ConferenceRoom) {
            return 1;
        } else if (reservationPlace instanceof Workplace) {
            return 2;
        }
        return 0;
    }
}