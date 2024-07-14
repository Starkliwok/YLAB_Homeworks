package com.Y_LAB.homework.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.Y_LAB.homework.validation.constants.FieldConstraintConstants.*;

/**
 * DTO Request объекта пользователя
 * @author Денис Попов
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String username;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;
}