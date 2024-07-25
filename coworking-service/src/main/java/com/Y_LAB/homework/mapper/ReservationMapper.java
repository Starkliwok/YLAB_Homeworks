package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Интерфейс для преобразования сущностей броней в DTO объект и наоборот
 * @author Денис Попов
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    /**
     * Преобразует объект брони в DTO Response объект брони
     * @param reservation Бронь
     * @return DTO Response объект брони
     */
    @Mapping(source = "reservation.reservationPlace.id", target = "reservationPlaceId")
    ReservationResponseDTO toResponseDTO(Reservation reservation);

    /**
     * Преобразует список объектов брони в список DTO Response объектов брони
     * @param reservations Список броней
     * @return Список DTO Response объектов брони
     */
    List<ReservationResponseDTO> toResponseDTOList(List<Reservation> reservations);

    /**
     * Преобразует параметры метода в объект брони
     * @param reservationRequestDTO DTO Request объект брони
     * @param reservationId Уникальный идентификатор брони
     * @param userId Уникальный идентификатор пользователя
     * @param reservationPlace Объект места для бронирования
     * @return объект брони
     */
    @Mapping(source = "reservationRequestDTO.startDate", target = "startDate")
    @Mapping(source = "reservationRequestDTO.endDate", target = "endDate")
    @Mapping(source = "reservationPlace", target = "reservationPlace")
    @Mapping(source = "reservationId", target = "id")
    @Mapping(source = "userId", target = "userId")
    Reservation toReservation(ReservationRequestDTO reservationRequestDTO, long reservationId, long userId,
                                         ReservationPlace reservationPlace);
}