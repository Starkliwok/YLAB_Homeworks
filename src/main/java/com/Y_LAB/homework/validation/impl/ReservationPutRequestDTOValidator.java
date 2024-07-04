package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.ReservationPutRequestDTO;
import com.Y_LAB.homework.validation.NumberValidator;
import com.Y_LAB.homework.validation.ValidatorDTO;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.*;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.FIELD_END_DATE;

public class ReservationPutRequestDTOValidator implements ValidatorDTO<ReservationPutRequestDTO> {

    private static ReservationPutRequestDTOValidator instance;

    private static NumberValidator numberValidator;

    private ReservationPutRequestDTOValidator() {
        numberValidator = NumberValidator.getInstance();
    }
    public static ReservationPutRequestDTOValidator getInstance() {
        if (instance == null) {
            instance = new ReservationPutRequestDTOValidator();
        }
        return instance;
    }

    @Override
    public void validate(ReservationPutRequestDTO dto) throws FieldNotValidException {
        numberValidator.validate(dto.getId(), FIELD_ID);
        numberValidator.validate(dto.getReservationPlaceId(), FIELD_RESERVATION_PLACE_ID);
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new FieldNotValidException(FIELD_START_DATE, "не может быть в прошедшем времени");
        } else if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new FieldNotValidException(FIELD_END_DATE, "не может быть раньше даты начала");
        }
    }
}
