package com.Y_LAB.homework.validation;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;


/**
 * Интерфейс описывает валидацию объектов
 * @author Денис Попов
 * @version 2.0
 */
public interface ValidatorDTO<T> {

    /**
     * Проверяет поля переданного объекта на валидность
     * @param dto объект подлежащий валидации
     * @throws FieldNotValidException если объект не валидный
     */
    void validate(T dto) throws FieldNotValidException;
}
