package com.Y_LAB.homework.util.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для получения параметров из файла
 * @author Денис Попов
 * @version 1.0
 */
public class PropertiesLoader {

    /** Статическое поле для параметров*/
    private static Properties properties;

    private PropertiesLoader(){}

    /**
     * Метод возвращает объект {@link Properties}, в случае если созданного объекта не существует.
     * @param path путь к файлу .properties
     * @return объект {@link Properties}
     */
    public static Properties getProperties(String path){
        if (properties == null) {
            properties = new Properties();
            try(InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream(path)) {
                properties.load(in);
            } catch (IOException e) {
                System.out.println("Произошла ошибка " + e.getMessage() + ", приложение завершает работу");
                System.exit(-1);
            }
        }
        return properties;
    }
}
