package com.Y_LAB.homework.in.servlet.admin;

import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
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

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServletTest {
    private UserAdminServlet userAdminServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private PrintWriter writer;
    @Mock
    private HttpSession session;
    private UserRequestDTO realUser;

    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(USER_SERVICE)).thenReturn(userService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        userAdminServlet = new UserAdminServlet();
        userAdminServlet.init(servletConfig);
        realUser = new AdminRequestDTO();
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 401, если пользователь не авторизован")
    void userAdminTest_should_Return_Code_401_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        userAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и вернуть тело ответа со всеми пользователями")
    void userAdminTest_should_Return_Code_200_GET_All_Reservations() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        userAdminServlet.doGet(request, response);

        verify(userService, times(1)).getAllUsers();
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 403 если сессия не содержит администратора")
    void userAdminTest_should_Return_Code_403_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());

        userAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 400 когда идентификатор пользователя не валидный")
    void userAdminTest_should_Return_Code_400_GET_When_ID_IsNotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432dsa");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        userAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, если пользователя с таким id не существует")
    void userAdminTest_should_Return_Code_404_GET_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("436542");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(userService.getUser(436542)).thenReturn(null);

        userAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и пользователя, если пользователь с таким id существует")
    void userAdminTest_should_Return_Code_200_GET_WhenReservationExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("54354354");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(userService.getUser(54354354)).thenReturn(new User(1, "rook", "look"));

        userAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда пользователя с таким id не существует")
    void userAdminTest_should_Return_Code_404_DELETE_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        userAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 403 если сессия не содержит администратора")
    void userAdminTest_should_Return_Code_403_DELETE() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);

        userAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда id невалиден")
    void userAdminTest_should_Return_Code_404_DELETE_When_ID_NotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1ds");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        userAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404 если не указан идентификатор")
    void userAdminTest_should_Return_Code_404_DELETE_When_ID_IsNull() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        userAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 401, если пользователь не авторизован")
    void userAdminTest_should_Return_Code_401_DELETE() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        userAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 200 и удалить пользователя")
    void userAdminTest_should_Return_Code_200_DELETE_Reservation() throws IOException {
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(userService.getUser(1)).thenReturn(new User(4, "lpp", "dkk"));

        userAdminServlet.doDelete(request, response);

        verify(userService).deleteUser(4);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}