package com.Y_LAB.homework.in.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletPathUtil {
    public static String getPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) {
            return null;
        }
        return path.replace("/", "");
    }
}
