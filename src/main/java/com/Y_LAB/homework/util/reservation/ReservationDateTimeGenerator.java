package com.Y_LAB.homework.util.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.Y_LAB.homework.constants.ReservationConstants.*;

/**
 * Класс для генерирования дат для бронирования мест
 * @author Денис Попов
 * @version 1.0
 */
public class ReservationDateTimeGenerator {

    /**
     * Метод генерирует даты на {@link com.Y_LAB.homework.constants.ReservationConstants#RESERVATION_PERIOD} дней вперёд
     * @return коллекция с датами
     */
    public static List<LocalDate> generateDates() {
        List<LocalDate> availableDates = new ArrayList<>();
        for(int i = 0; i <= RESERVATION_PERIOD; i++) {
            availableDates.add(LocalDate.now().plusDays(i));
        }
        return availableDates;
    }

    /**
     * Метод генерирует временные промежутки от
     * {@link com.Y_LAB.homework.constants.ReservationConstants#START_HOUR_FOR_RESERVATION} <br>
     * до {@link com.Y_LAB.homework.constants.ReservationConstants#END_HOUR_FOR_RESERVATION} <br> количества часов
     * в виде ключа=значение в качестве одиночных слотов длительностью в 1 час, например (08:00=09:00, 09:00-10:00)
     * @return коллекция с временными промежутками в виде ключа=значения
     */
    public static Map<LocalTime, LocalTime> generateTimesPeriod() {
        Map<LocalTime, LocalTime> availableTimes = new LinkedHashMap<>();
        for(int j = START_HOUR_FOR_RESERVATION; j < END_HOUR_FOR_RESERVATION; j++) {
            availableTimes.put(
                    LocalTime.of(j, 0, 0, 0),
                    LocalTime.of(j + 1, 0, 0, 0));
        }
        return availableTimes;
    }
}
