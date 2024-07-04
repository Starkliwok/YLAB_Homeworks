package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.validation.NumberValidator;
import com.Y_LAB.homework.validation.ValidatorDTO;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.*;

public class ReservationRequestDTOValidator implements ValidatorDTO<ReservationRequestDTO> {

    private static ReservationRequestDTOValidator instance;

    private static NumberValidator numberValidator;

    private ReservationRequestDTOValidator() {
        numberValidator = NumberValidator.getInstance();
    }
    public static ReservationRequestDTOValidator getInstance() {
        if (instance == null) {
            instance = new ReservationRequestDTOValidator();
        }
        return instance;
    }

    @Override
    public void validate(ReservationRequestDTO dto) throws FieldNotValidException {
        numberValidator.validate(dto.getReservationPlaceId(), FIELD_RESERVATION_PLACE_ID);
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new FieldNotValidException(FIELD_START_DATE, "не может быть в прошедшем времени");
        } else if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new FieldNotValidException(FIELD_END_DATE, "не может быть раньше даты начала");
        }
    }
}
