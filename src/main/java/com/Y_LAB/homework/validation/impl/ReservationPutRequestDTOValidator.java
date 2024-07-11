package com.Y_LAB.homework.validation.impl;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.ReservationPutRequestDTO;
import com.Y_LAB.homework.validation.NumberValidator;
import com.Y_LAB.homework.validation.ValidatorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.*;

/**
 * Класс для валидации объектов {@link ReservationPutRequestDTOValidator}
 * @author Денис Попов
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
public class ReservationPutRequestDTOValidator implements ValidatorDTO<ReservationPutRequestDTO> {

    private final NumberValidator numberValidator;

    /**{@inheritDoc}*/
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
