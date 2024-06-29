package com.Y_LAB.homework.in.util;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleReaderTest {

    @Test
    @DisplayName("Метод должен вернуть искусственный ввод")
    void readPageChooseShouldReturnInput() {
        String simulatedInput = "1";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));
        int expected = Integer.parseInt(simulatedInput);

        int actual = ConsoleReader.readPageChoose();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть -1")
    void readPageChooseShouldReturnNegativeOne() {
        String simulatedInput = "fds";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));
        int expected = -1;

        int actual = ConsoleReader.readPageChoose();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен преобразовать ввод строчки в LocalDate")
    void readDate() {
        String simulatedInput = "10-06-2024";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));
        LocalDate expected = LocalDate.of(2024, 6, 10);

        LocalDate actual = ConsoleReader.readDate();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен преобразовать ввод строчки в LocalTime")
    void readHour() {
        String simulatedInput = "16";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));
        LocalTime expected = LocalTime.of(16, 0, 0);

        LocalTime actual = ConsoleReader.readHour();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть строчку искусственного ввода")
    void readStringValue() {
        String expected = "16";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(expected.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));

        String actual = ConsoleReader.readStringValue();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть число искусственного ввода")
    void readIntValue() {
        String simulatedInput = "16";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));
        int expected = 16;

        int actual = ConsoleReader.readIntValue();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Метод должен вернуть дробное число искусственного ввода")
    void readDoubleValue() {
        String simulatedInput = "16.3";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(byteArrayInputStream);
        ConsoleReader.setScanner(new Scanner(byteArrayInputStream));
        double expected = 16.3;

        double actual = ConsoleReader.readDoubleValue();

        assertThat(actual).isEqualTo(expected);
    }
}