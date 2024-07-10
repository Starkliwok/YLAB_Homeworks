package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.service.ReservationService;
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

import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationAdminServletTest {
    private ReservationAdminServlet reservationAdminServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ReservationService reservationService;
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
        when(servletContext.getAttribute(RESERVATION_SERVICE)).thenReturn(reservationService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        reservationAdminServlet = new ReservationAdminServlet();
        reservationAdminServlet.init(servletConfig);
        realUser = new AdminRequestDTO();
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 401, если пользователь не авторизован")
    void reservationTest_should_Return_Code_401_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и вернуть тело ответа со всеми бронями")
    void reservationTest_should_Return_Code_200_GET_All_Reservations() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationAdminServlet.doGet(request, response);

        verify(reservationService, times(1)).getAllReservations();
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 403 если сессия не содержит администратора")
    void reservationTest_should_Return_Code_403_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());

        reservationAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 400 когда идентификатор брони не валидный")
    void reservationTest_should_Return_Code_400_GET_When_ID_IsNotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432dsa");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, если бронь с таким id не существует")
    void reservationTest_should_Return_Code_404_GET_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("436542");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(436542)).thenReturn(null);

        reservationAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и бронь, если бронь с таким id существует")
    void reservationTest_should_Return_Code_200_GET_WhenReservationExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("54354354");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(54354354)).thenReturn(new Reservation());

        reservationAdminServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда места с таким id не существует")
    void reservationTest_should_Return_Code_404_DELETE_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 403 если сессия не содержит администратора")
    void reservationTest_should_Return_Code_403_DELETE() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);

        reservationAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда id невалиден")
    void reservationTest_should_Return_Code_404_DELETE_When_ID_NotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1ds");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404 если не указан идентификатор")
    void reservationTest_should_Return_Code_404_DELETE_When_ID_IsNull() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 401, если пользователь не авторизован")
    void reservationTest_should_Return_Code_401_DELETE() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationAdminServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 200 и удалить бронь")
    void reservationTest_should_Return_Code_200_DELETE_Reservation() throws IOException {
        Reservation reservation = new Reservation();
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(1)).thenReturn(reservation);
        when(response.getWriter()).thenReturn(writer);

        reservationAdminServlet.doDelete(request, response);

        verify(reservationService).deleteReservation(1);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}