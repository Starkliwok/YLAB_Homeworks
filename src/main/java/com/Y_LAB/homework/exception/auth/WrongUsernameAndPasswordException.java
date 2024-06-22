package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link Exception} и описывает нарушение формата логина во время регистрации пользователя
 * @author Денис Попов
 * @version 1.0
 */
public class WrongUsernameAndPasswordException extends Exception {
    public WrongUsernameAndPasswordException(String message) {
        super(message);
    }
}
