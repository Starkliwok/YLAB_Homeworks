package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link Exception} используется в случае если у пользователя недостаточно прав для совершения действия
 * @author Денис Попов
 * @version 1.0
 */
public class NotEnoughRightsException extends Exception {
    public NotEnoughRightsException(String message) {
        super(message);
    }
}