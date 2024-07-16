package com.Y_LAB.homework.exception;

/**
 * Класс наследуется от {@link Exception} и описывает случаи, когда искомый объект не найден
 * @author Денис Попов
 * @version 1.0
 */
public class ObjectNotFoundException extends Exception {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
