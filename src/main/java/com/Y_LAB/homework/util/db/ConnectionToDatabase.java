package com.Y_LAB.homework.util.db;

import com.Y_LAB.homework.util.init.PropertiesLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;

/**
 * Класс для подключения к базе данных
 * @author Денис Попов
 * @version 1.0
 */
public class ConnectionToDatabase {

    /** Статическое поле подключения к базе данных*/
    private static Connection conn;

    private ConnectionToDatabase() {}

    /**
     * Метод возвращает подключение, в случае если созданного подключения не существует или оно закрыто.
     * Вызывает метод {@link PropertiesLoader#getProperties(String)} для получения параметров для создания подключения
     * @return подключение к базе данных
     */
    public static Connection getConnection() {
        Properties properties = PropertiesLoader.getProperties(PROPERTIES_PATH);
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection
                        (properties.getProperty(PROPERTIES_URL_KEY)
                        , properties.getProperty(PROPERTIES_USERNAME_KEY)
                        , properties.getProperty(PROPERTIES_PASSWORD_KEY));
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, " + e.getMessage() + ", приложение завершает работу");
            System.exit(-1);
        }
        return conn;
    }

    /**
     * Метод возвращает подключение, которое создает благодаря получаемым данным из объекта {@link Properties}
     * Вызывает методы {@link Properties#getProperty(String)} для получения параметров для создания подключения
     * @param properties объект класса {@link Properties}
     * @return подключение к базе данных
     */
    public static Connection getConnectionFromProperties(Properties properties) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    properties.getProperty(PROPERTIES_URL_KEY)
                    , properties.getProperty(PROPERTIES_USERNAME_KEY)
                    , properties.getProperty(PROPERTIES_PASSWORD_KEY));
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, " + e.getMessage() + ", приложение завершает работу");
            System.exit(-1);
        }
        return connection;
    }
}