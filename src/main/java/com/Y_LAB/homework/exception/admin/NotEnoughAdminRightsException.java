package com.Y_LAB.homework.exception.admin;

/**
 * Класс наследуется от {@link Exception} описывает случай, когда у администратора нет прав на выполнение каких либо
 * действий
 * @author Денис Попов
 * @version 1.0
 */
public class NotEnoughAdminRightsException extends Exception {
    public NotEnoughAdminRightsException(String message) {
        super(message);
    }
}