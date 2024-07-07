package com.Y_LAB.homework.in.servlet.admin;

import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.SortRequest;
import com.Y_LAB.homework.service.ReservationService;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationSortAdminServletTest {
    private ReservationSortAdminServlet reservationSortAdminServlet;
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

        reservationSortAdminServlet = new ReservationSortAdminServlet();
        reservationSortAdminServlet.init(servletConfig);
        realUser = new AdminRequestDTO();
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 401, если пользователь не авторизован")
    void reservationSortTest_should_Return_Code_401_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 403, если пользователь не является администратором")
    void reservationSortTest_should_Return_Code_403_POST_WhenUserWasNotAdmin() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);

        reservationSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тело запроса не соответствует требованиям")
    void reservationSortTest_should_Return_Code_400_POST_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), SortRequest.class)).thenThrow(JsonMappingException.class);

        reservationSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тип сортировки не соответствует доступным типам")
    void reservationSortTest_should_Return_Code_400_POST_WhenSortTypeIsNotValid() throws IOException {
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("gfdgd");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortRequest.class)).thenReturn(sortRequest);

        reservationSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 200, и вернуть все брони отсортированные по пользователям")
    void reservationSortTest_should_Return_Code_200_POST_SortByUsers() throws IOException {
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("user");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortRequest.class)).thenReturn(sortRequest);

        reservationSortAdminServlet.doPost(request, response);

        verify(reservationService).getAllReservationsByUsers();
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 200, и вернуть все брони отсортированные по дате")
    void reservationSortTest_should_Return_Code_200_POST_SortByDate() throws IOException {
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("date");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortRequest.class)).thenReturn(sortRequest);

        reservationSortAdminServlet.doPost(request, response);

        verify(reservationService).getAllReservationsByDate();
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}