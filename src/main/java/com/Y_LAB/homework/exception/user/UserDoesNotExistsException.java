package com.Y_LAB.homework.exception.user;

/**
 * Класс наследуется от {@link Exception} и описывает несуществование пользователя
 * @author Денис Попов
 * @version 1.0
 */
public class UserDoesNotExistsException extends Exception {
    public UserDoesNotExistsException(String message) {
        super(message);
    }
}
