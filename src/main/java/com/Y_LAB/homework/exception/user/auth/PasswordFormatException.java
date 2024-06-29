package com.Y_LAB.homework.exception.user.auth;

/**
 * Класс наследуется от {@link RegistrationException} и описывает нарушение формата пароля во время регистрации, авторизации пользователя
 * @author Денис Попов
 * @version 2.0
 */
public class PasswordFormatException extends RegistrationException {
    public PasswordFormatException(String message) {
        super(message);
    }
}
