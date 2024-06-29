package com.Y_LAB.homework.in.util;

import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Класс для считывания введённых данных пользователем
 * @author Денис Попов
 * @version 2.0
 */
public class ConsoleReader {

    /** Статическое поле сканера, предназначенное для считывания введённых данных*/
    @Setter
    private static Scanner scanner = new Scanner(System.in);

    private ConsoleReader() {}


    /**
     * Метод для считывания целочисленного значения, которое предназначено для выбора действий на различных панелях
     * @return пользовательский ввод, в случае ошибок преобразования возвращает -1
     */
    public static int readPageChoose() {
        int choose;
        try {
            choose = scanner.nextInt();
        } catch (InputMismatchException exception) {
            choose = -1;
            scanner.next();
        }
        return choose;
    }

    /**
     * Метод для считывания даты
     * @return пользовательский ввод преобразованный в {@link LocalDate},
     * в случае ошибок преобразования выводит сообщение об ошибке и рекурсивно вызывает себя
     */
    public static LocalDate readDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(scanner.next(), dateTimeFormatter);
        } catch (DateTimeParseException | InputMismatchException ex) {
            System.out.println("\nДата не соответствует формату \"ДД-ММ-ГГГГ\"");
            return readDate();
        }
        return localDate;
    }

    /**
     * Метод для считывания даты и времени
     * @return пользовательский ввод преобразованный в {@link LocalTime},
     * в случае ошибок преобразования выводит сообщение об ошибке и рекурсивно вызывает себя
     */
    public static LocalTime readHour() {
        LocalTime localTime;
        try {
            localTime = LocalTime.of(scanner.nextInt(), 0);
        } catch (DateTimeParseException | InputMismatchException ex) {
            System.out.println("\nПовторите попытку, введите час в формате \"ЧЧ\"");
            return readHour();
        }
        return localTime;
    }

    /**
     * Метод для считывания строки в консоли
     * @return пользовательский ввод
     */
    public static String readStringValue() {
        return scanner.next();
    }

    /**
     * Метод для считывания целочисленного значения
     * @return пользовательский ввод, в случае ошибок преобразования выводит сообщение об ошибке
     * и рекурсивно вызывает себя
     */
    public static int readIntValue() {
        int value;
        try {
            value = scanner.nextInt();
        } catch (InputMismatchException ex) {
            System.out.println("Повторите попытку, введите целое число");
            scanner.next();
            return readIntValue();
        }
        return value;
    }

    /**
     * Метод для считывания числа с плавающей точкой
     * @return пользовательский ввод, в случае ошибок преобразования выводит сообщение об ошибке
     * и рекурсивно вызывает себя
     */
    public static double readDoubleValue() {
        double value;
        try {
            value = scanner.nextDouble();
        } catch (InputMismatchException ex) {
            System.out.println("Повторите попытку, введите целое или дробное число");
            scanner.next();
            return readDoubleValue();
        }
        return value;
    }
}
