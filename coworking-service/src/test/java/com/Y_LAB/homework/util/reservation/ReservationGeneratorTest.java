package com.Y_LAB.homework.util.reservation;

import com.Y_LAB.homework.reservation.ReservationDateTimeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.Y_LAB.homework.constants.ReservationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationGeneratorTest {

    @Test
    @DisplayName("Метод должен сгенерировать даты для бронирования")
    void generateDates() {
        List<LocalDate> actual = ReservationDateTimeGenerator.generateDates();
        List<LocalDate> expected = new ArrayList<>();
        for(int i = 0; i <= RESERVATION_PERIOD; i++) {
            expected.add(LocalDate.now().plusDays(i));
        }
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен сгенерировать временные пары для бронирования")
    void generateTimesPeriod() {
        Map<LocalTime, LocalTime> actual = ReservationDateTimeGenerator.generateTimesPeriod();
        Map<LocalTime, LocalTime> expected = new LinkedHashMap<>();
        for(int j = START_HOUR_FOR_RESERVATION; j < END_HOUR_FOR_RESERVATION; j++) {
            expected.put(
                    LocalTime.of(j, 0, 0, 0),
                    LocalTime.of(j + 1, 0, 0, 0));
        }
        assertThat(actual).isEqualTo(expected);
    }
}