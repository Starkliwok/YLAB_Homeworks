package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.OBJECT_MAPPER;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_LOGOUT_PATH;

@WebServlet(CONTROLLER_LOGOUT_PATH)
public class LogoutServlet extends HttpServlet {
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    /**
     * @param req  запрос на выход из приложения
     * @param resp ответ пользователю статус 204 и удаление его сессии
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute(SESSION_USER) != null) {
            session.invalidate();
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
        }
    }
}