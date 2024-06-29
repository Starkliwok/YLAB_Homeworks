package com.Y_LAB.homework.exception.user.auth;

/**
 * Класс наследуется от {@link Exception} и описывает проблемы при регистрации пользователя
 * @author Денис Попов
 * @version 1.0
 */
public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
}