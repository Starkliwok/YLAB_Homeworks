package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceFullRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.validation.NumberValidator;
import com.Y_LAB.homework.validation.ValidatorDTO;

import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.NAME_MAX_LENGTH;
import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.NAME_MIN_LENGTH;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.*;

public class ReservationPlaceFullRequestDTOValidator implements ValidatorDTO<ReservationPlaceFullRequestDTO> {

    private static ReservationPlaceFullRequestDTOValidator instance;

    private static NumberValidator numberValidator;

    private ReservationPlaceFullRequestDTOValidator() {
        numberValidator = NumberValidator.getInstance();
    }
    public static ReservationPlaceFullRequestDTOValidator getInstance() {
        if (instance == null) {
            instance = new ReservationPlaceFullRequestDTOValidator();
        }
        return instance;
    }

    @Override
    public void validate(ReservationPlaceFullRequestDTO dto) throws FieldNotValidException {
        numberValidator.validate(dto.getId(), FIELD_ID);
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
