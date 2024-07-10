package com.Y_LAB.homework.config;

import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;

/**
 * Компонент - интерцептор для авторизации запросов.
 */
@Component
@RequiredArgsConstructor
public class AuthorizationConfig implements HandlerInterceptor {

    /**
     * Множество URL-путей, которые игнорируются при проверке авторизации.
     */
    private final Set<String> ignoreMethods = new HashSet<>();

    /**
     * Множество URL-путей, которые дополнительно проверяются на роль администратора.
     */
    private final Set<String> requiredAdminMethods = new HashSet<>();

    {
        ignoreMethods.add("/user/register");
        ignoreMethods.add("/user/login");
        ignoreMethods.add("/v3/api-docs");
        requiredAdminMethods.add("/meter/add");
        requiredAdminMethods.add("/user/user-actions");
        requiredAdminMethods.add("/user/users");
    }

    /**
     * Перехватчик, выполняющий предварительную обработку запроса для проверки авторизации пользователей.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @param handler  Обрабатываемый объект.
     * @return {@code true}, если запрос может быть обработан; в противном случае - {@code false}.
     * @throws Exception Исключение, если произошла ошибка при обработке запроса.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpSession session = request.getSession();
        UserRequestDTO currentUser;
        AdminRequestDTO currentAdmin;
        if (ignoreMethods.contains(path) || path.toLowerCase(Locale.ROOT).contains("swagger")) {
            return true;
        } else if (requiredAdminMethods.contains(path)) {
            try {
                currentAdmin = (AdminRequestDTO) session.getAttribute(SESSION_USER);
                if (currentAdmin == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
            } catch (ClassCastException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            return true;
        }
        currentUser = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}