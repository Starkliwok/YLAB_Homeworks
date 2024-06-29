package com.Y_LAB.homework.util.init;

import java.io.FileInputStream;
import java.io.IOException;
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
            try(FileInputStream in = new FileInputStream(path)){
                properties.load(in);
            } catch (IOException e) {
                System.out.println("Произошла ошибка, приложение завершает работу");
                System.exit(-1);
            }
        }
        return properties;
    }
}
