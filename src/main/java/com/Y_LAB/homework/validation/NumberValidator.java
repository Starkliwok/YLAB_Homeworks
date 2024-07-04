package com.Y_LAB.homework.validation;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberValidator {
    private static NumberValidator instance;
    public static NumberValidator getInstance() {
        if (instance == null) {
            instance = new NumberValidator();
        }
        return instance;
    }

    public void validate(Object number, String name) throws FieldNotValidException {
        if (!NumberUtils.isCreatable(String.valueOf(number))) {
            throw new FieldNotValidException(name, "должен быть числом");
        } else if (number instanceof Long ln && ln < 1L
                || number instanceof Float fl && fl < 1.0f
                || number instanceof Integer inr && inr < 1) {
            throw new FieldNotValidException(name, "должен быть положительным");
        }
    }
}