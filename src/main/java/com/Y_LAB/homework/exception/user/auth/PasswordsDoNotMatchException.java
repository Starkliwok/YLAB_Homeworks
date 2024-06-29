package com.Y_LAB.homework.exception.user.auth;

/**
 * Класс наследуется от {@link RegistrationException} и описывает <b>несоответствие введённого пароля и повторение
 * пароля</b> для устранения опечаток со стороны пользователя при первом вводе пароля во время регистрации пользователя
 * @author Денис Попов
 * @version 2.0
 */
public class PasswordsDoNotMatchException extends RegistrationException {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
