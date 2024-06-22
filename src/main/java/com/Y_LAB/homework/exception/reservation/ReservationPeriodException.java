package com.Y_LAB.homework.exception.reservation;

/**
 * Класс наследуется от {@link Exception} описывает несоответствии указанных дат для бронирования места с доступными датами
 * @author Денис Попов
 * @version 1.0
 */
public class ReservationPeriodException extends Exception {
    public ReservationPeriodException(String message) {
        super(message);
    }
}
