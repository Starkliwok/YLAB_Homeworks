package com.Y_LAB.homework.in.servlet.admin;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.in.servlet.user.ReservationServlet;
import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.UserMapperImpl;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.AuditResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.AuditService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAdminServletTest {
    private AuditAdminServlet auditAdminServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuditService auditService;
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
        when(servletContext.getAttribute(AUDIT_SERVICE)).thenReturn(auditService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        auditAdminServlet = new AuditAdminServlet();
        auditAdminServlet.init(servletConfig);
        realUser = new AdminRequestDTO();
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 401, если пользователь не авторизован")
    void auditTest_should_Return_Code_401_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        auditAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и вернуть тело ответа со всеми аудитами")
    void auditTest_should_Return_Code_200_GET_All_Reservations() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        auditAdminServlet.doGet(request, response);

        verify(auditService, times(1)).getAllAudits();
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 403 если сессия не содержит администратора")
    void auditTest_should_Return_Code_403_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());

        auditAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 400 когда идентификатор аудита не валидный")
    void auditTest_should_Return_Code_400_GET_When_ID_IsNotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432dsa");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        auditAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, если аудит с таким id не существует")
    void auditTest_should_Return_Code_404_GET_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("436542");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        auditAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и аудит, если аудит с таким id существует")
    void auditTest_should_Return_Code_200_GET_WhenReservationExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("54354354");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(auditService.getAudit(54354354)).thenReturn(new Audit(2L, "someAction"));

        auditAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}