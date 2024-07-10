package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.IdWithLocalDateRequest;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
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
import java.time.LocalDate;

import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceTimeSlotServletTest {

    private ReservationPlaceTimeSlotServlet reservationPlaceTimeSlotServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ReservationPlaceService reservationPlaceService;
    @Mock
    private FreeReservationSlotService freeReservationSlotService;
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
    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(FREE_RESERVATION_SLOT_SERVICE)).thenReturn(freeReservationSlotService);
        when(servletContext.getAttribute(RESERVATION_PLACE_SERVICE)).thenReturn(reservationPlaceService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        reservationPlaceTimeSlotServlet = new ReservationPlaceTimeSlotServlet();
        reservationPlaceTimeSlotServlet.init(servletConfig);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тело запроса не соответствует требованиям")
    void timeSlotTest_should_Return_Code_400_POST_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), IdWithLocalDateRequest.class)).thenThrow(JsonMappingException.class);

        reservationPlaceTimeSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 401, если пользователь не авторизован")
    void timeSlotTest_should_Return_Code_401_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceTimeSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если объект в теле не валиден")
    void timeSlotTest_should_Return_Code_400_POST_WhenObjectIsNotValid() throws IOException {
        IdWithLocalDateRequest idWithLocalDateRequest = new IdWithLocalDateRequest();
        idWithLocalDateRequest.setLocalDate(LocalDate.now());
        idWithLocalDateRequest.setId(-1);
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(objectMapper.readValue(request.getReader(), IdWithLocalDateRequest.class)).thenReturn(idWithLocalDateRequest);

        reservationPlaceTimeSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 201, и сохранить место для бронирования")
    void timeSlotTest_should_Return_Code_201_POST() throws IOException {
        IdWithLocalDateRequest idWithLocalDateRequest = new IdWithLocalDateRequest();
        idWithLocalDateRequest.setLocalDate(LocalDate.now());
        idWithLocalDateRequest.setId(1);
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(objectMapper.readValue(request.getReader(), IdWithLocalDateRequest.class)).thenReturn(idWithLocalDateRequest);
        when(reservationPlaceService.getReservationPlace(1)).thenReturn(new Workplace());

        reservationPlaceTimeSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}