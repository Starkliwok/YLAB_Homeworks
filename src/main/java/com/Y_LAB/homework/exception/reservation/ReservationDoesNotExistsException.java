package com.Y_LAB.homework.exception.reservation;

/**
 * Класс наследуется от {@link Exception} описывает несуществование брони
 * @author Денис Попов
 * @version 1.0
 */
public class ReservationDoesNotExistsException extends Exception {
    public ReservationDoesNotExistsException(String message) {
        super(message);
    }
}

