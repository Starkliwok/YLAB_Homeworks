package com.Y_LAB.homework.exception.reservation;

/**
 * Класс наследуется от {@link Exception} описывает случай, когда нет свободных дат для бронирования места
 * @author Денис Попов
 * @version 1.0
 */
public class DatesForReservationDoesNotExistsException extends Exception {
    public DatesForReservationDoesNotExistsException(String message) {
        super(message);
    }
}
