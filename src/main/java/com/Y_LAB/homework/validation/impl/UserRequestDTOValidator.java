package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.validation.ValidatorDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.*;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.FIELD_PASSWORD;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.FIELD_USERNAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequestDTOValidator implements ValidatorDTO<UserRequestDTO> {

    private static UserRequestDTOValidator instance;

    public static UserRequestDTOValidator getInstance() {
        if (instance == null) {
            instance = new UserRequestDTOValidator();
        }
        return instance;
    }

    @Override
    public void validate(UserRequestDTO dto) throws FieldNotValidException {
        if (dto.getUsername().length() < LOGIN_MIN_LENGTH || dto.getUsername().length() > LOGIN_MAX_LENGTH) {
            throw new FieldNotValidException(FIELD_USERNAME,
                    "количество символов в поле должно находиться в диапазоне от " +
                            LOGIN_MIN_LENGTH + " до " + LOGIN_MAX_LENGTH + " символов");
        } else if (dto.getPassword().length() < PASSWORD_MIN_LENGTH || dto.getPassword().length() > PASSWORD_MAX_LENGTH) {
            throw new FieldNotValidException(FIELD_PASSWORD,
                    "количество символов в поле должно находиться в диапазоне от " +
                            PASSWORD_MIN_LENGTH + " до " + PASSWORD_MAX_LENGTH + " символов");
        }
    }
}
