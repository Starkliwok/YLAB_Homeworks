package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link Exception} и описывает <b>несоответствие введённого пароля и повторение пароля</b> для устранения опечаток со стороны
 * пользователя при первом вводе пароля при регистрации
 * @author Денис Попов
 * @version 1.0
 */
public class PasswordsDoNotMatchException extends Exception {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
