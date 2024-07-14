package com.Y_LAB.homework.exception.user.auth;

/**
 * Класс наследуется от {@link Exception} и описывает проблемы при авторизации пользователя
 * @author Денис Попов
 * @version 1.0
 */
public class AuthorizeException extends Exception {
    public AuthorizeException(String message) {
        super(message);
    }
}