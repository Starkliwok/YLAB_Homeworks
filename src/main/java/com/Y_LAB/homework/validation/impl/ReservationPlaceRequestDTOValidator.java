package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.validation.NumberValidator;
import com.Y_LAB.homework.validation.ValidatorDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.*;
import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.PASSWORD_MAX_LENGTH;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.*;

public class ReservationPlaceRequestDTOValidator implements ValidatorDTO<ReservationPlaceRequestDTO> {

    private static ReservationPlaceRequestDTOValidator instance;

    private static NumberValidator numberValidator;

    private ReservationPlaceRequestDTOValidator() {
        numberValidator = NumberValidator.getInstance();
    }
    public static ReservationPlaceRequestDTOValidator getInstance() {
        if (instance == null) {
            instance = new ReservationPlaceRequestDTOValidator();
        }
        return instance;
    }

    @Override
    public void validate(ReservationPlaceRequestDTO dto) throws FieldNotValidException {
        numberValidator.validate(dto.getTypeId(), FIELD_TYPE_ID);
        numberValidator.validate(dto.getPlaceArea(), FIELD_PLACE_AREA);
        numberValidator.validate(dto.getCostPerHour(), FIELD_COST_PER_HOUR);
        numberValidator.validate(dto.getNumberOfSeats(), FIELD_NUMBER_OF_SEATS);
        if (dto.getName().length() < NAME_MIN_LENGTH || dto.getName().length() > NAME_MAX_LENGTH) {
            throw new FieldNotValidException(FIELD_NAME,
                    "количество символов в поле должно находиться в диапазоне от " +
                            NAME_MIN_LENGTH + " до " + NAME_MAX_LENGTH + " символов");
        }
    }
}
