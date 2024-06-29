package com.Y_LAB.homework.exception.user.auth;

/**
 * Класс наследуется от {@link RegistrationException} и описывает нарушение формата логина во время регистрации пользователя
 * @author Денис Попов
 * @version 2.0
 */
public class UsernameFormatException extends RegistrationException {
    public UsernameFormatException(String message) {
        super(message);
    }
}
