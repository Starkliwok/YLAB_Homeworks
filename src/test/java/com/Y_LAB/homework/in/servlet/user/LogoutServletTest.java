package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.OBJECT_MAPPER;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.SESSION_USER;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServletTest {

    private LogoutServlet logoutServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private HttpSession session;
    @Mock
    private PrintWriter writer;

    private UserRequestDTO realUser;

    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        logoutServlet = new LogoutServlet();
        logoutServlet.init(servletConfig);
        realUser = new UserRequestDTO("root", "root");
    }

    @Test
    @DisplayName("Проверка на успешный выход")
    void logoutTest_should_Logout() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(response.getWriter()).thenReturn(writer);

        logoutServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Проверка на неуспешный выход, если пользователь не авторизован")
    void logoutTest_should_NotLogout_WhenUserNotAuthorize() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);

        logoutServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}