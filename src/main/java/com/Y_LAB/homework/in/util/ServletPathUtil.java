package com.Y_LAB.homework.in.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Утилитарный класс для работы с URL сервлета
 * @author Денис Попов
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletPathUtil {

    /**
     * Метод для получения пути из http запроса
     * @param req http запрос
     * @return путь http запроса
     */
    public static String getPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) {
            return null;
        }
        return path.replace("/", "");
    }
}
