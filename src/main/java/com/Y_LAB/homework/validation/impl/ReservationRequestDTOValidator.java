package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.implementation.FreeReservationSlotServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.validation.NumberValidator;
import com.Y_LAB.homework.validation.ValidatorDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static com.Y_LAB.homework.constants.ReservationConstants.END_HOUR_FOR_RESERVATION;
import static com.Y_LAB.homework.constants.ReservationConstants.START_HOUR_FOR_RESERVATION;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.*;

/**
 * Класс для валидации объектов {@link ReservationRequestDTOValidator}
 * @author Денис Попов
 * @version 2.0
 */
public class ReservationRequestDTOValidator implements ValidatorDTO<ReservationRequestDTO> {

    private static ReservationRequestDTOValidator instance;

    private static NumberValidator numberValidator;

    private final ReservationPlaceService reservationPlaceService;

    private final FreeReservationSlotService freeReservationSlotService;

    private ReservationRequestDTOValidator() {
        numberValidator = NumberValidator.getInstance();
        reservationPlaceService = new ReservationPlaceServiceImpl();
        freeReservationSlotService = new FreeReservationSlotServiceImpl();
    }
    public static ReservationRequestDTOValidator getInstance() {
        if (instance == null) {
            instance = new ReservationRequestDTOValidator();
        }
        return instance;
    }

    /**{@inheritDoc}*/
    @Override
    public void validate(ReservationRequestDTO dto) throws FieldNotValidException {
        numberValidator.validate(dto.getReservationPlaceId(), FIELD_RESERVATION_PLACE_ID);
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new FieldNotValidException(FIELD_START_DATE, "не может быть в прошедшем времени");
        } else if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new FieldNotValidException(FIELD_END_DATE, "не может быть раньше даты начала");
        } else if (dto.getStartDate().getHour() < START_HOUR_FOR_RESERVATION || dto.getStartDate().getHour() > END_HOUR_FOR_RESERVATION) {
            throw new FieldNotValidException(FIELD_START_DATE, "время работы сервиса с 08:00 до 22:00");
        } else if (dto.getEndDate().getHour() > END_HOUR_FOR_RESERVATION || dto.getEndDate().getHour() < START_HOUR_FOR_RESERVATION) {
            throw new FieldNotValidException(FIELD_END_DATE, "время работы сервиса с 08:00 до 22:00");
        } else if (Duration.between(dto.getStartDate(), dto.getEndDate()).toHours() > 14) {
            throw new FieldNotValidException("date", "время работы сервиса с 08:00 до 22:00");
        }
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(dto.getReservationPlaceId());
        if(reservationPlace == null) {
            throw new FieldNotValidException(FIELD_RESERVATION_PLACE_ID, "места с таким id не существует");
        }
        Map<LocalTime, LocalTime> availableTimesInDate =
                freeReservationSlotService.getAllAvailableTimesForReservation(reservationPlace, dto.getEndDate().toLocalDate());
        LocalDateTime startDateTime = dto.getStartDate();
        LocalDateTime endDateTime = dto.getEndDate();

        int availableStartHour = availableTimesInDate.values().stream().toList().get(0).getHour() - 1;
        int availableEndHour = availableTimesInDate.values().stream().toList().get(availableTimesInDate.size() - 1).getHour();
        if(endDateTime.getHour() <= startDateTime.getHour()
                || endDateTime.getHour() > availableEndHour
                || endDateTime.getHour() < availableStartHour
                || startDateTime.getHour() < availableStartHour) {
            throw new FieldNotValidException("date", "Место невозможно забронировать в данный промежуток времени");
        }
        for(int i = startDateTime.getHour(); i < endDateTime.getHour(); i++) {
            if(!availableTimesInDate.containsKey(startDateTime.withHour(i).toLocalTime())) {
                throw new FieldNotValidException("date", "Место невозможно забронировать в данный промежуток времени");
            }
        }
    }
}
