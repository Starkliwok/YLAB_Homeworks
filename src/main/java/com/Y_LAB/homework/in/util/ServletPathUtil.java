package com.Y_LAB.homework.in.util;

import jakarta.servlet.http.HttpServletRequest;

public class ServletPathUtil {

    private ServletPathUtil() {}

    public static String getPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) {
            return null;
        }
        return path.replace("/", "");
    }
}
