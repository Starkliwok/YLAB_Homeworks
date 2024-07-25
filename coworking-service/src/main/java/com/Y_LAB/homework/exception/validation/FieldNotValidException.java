package com.Y_LAB.homework.exception.validation;

/**
 * Класс наследуется от {@link Exception} используется в случае, если поля объекта не соответствуют валидным полям
 * @author Денис Попов
 * @version 1.0
 */
public class FieldNotValidException extends Exception {
    public FieldNotValidException(String resource, String reason) {
        super(getErrorMessage(resource, reason));
    }

    private static String getErrorMessage(String resource, String reason) {
        return String.format("[%s] не валидный по причине: [%s]", resource, reason);
    }
}