package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link Exception} и описывает нарушение формата пароля во время регистрации, авторизации пользователя
 * @author Денис Попов
 * @version 1.0
 */
public class PasswordFormatException extends Exception {
    public PasswordFormatException(String message) {
        super(message);
    }
}
