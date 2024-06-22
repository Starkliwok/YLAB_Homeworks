package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link Exception} и описывает выход за рамки длины поля
 * @author Денис Попов
 * @version 1.0
 */
public class FieldMinimumLengthException extends Exception {
    public FieldMinimumLengthException(String message) {
        super(message);
    }
}