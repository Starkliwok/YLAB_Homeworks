package com.Y_LAB.homework.validation;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;

public interface ValidatorDTO<T> {

    void validate(T dto) throws FieldNotValidException;
}
