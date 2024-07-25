package com.Y_LAB.homework.validation;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * Интерфейс описывает валидацию чисел
 * @author Денис Попов
 * @version 2.0
 */
@Component
public class NumberValidator {

    /**
     * Проверяет переданное число на валидность
     * @param number число подлежащий валидации
     * @param name название поля, которое содержит переданное число
     * @throws FieldNotValidException если число не валидно
     */
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