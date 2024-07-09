package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.LocalDateRequest;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
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
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceDateSlotServletTest {
    private ReservationPlaceDateSlotServlet reservationPlaceDateSlotServlet;
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
    private UserRequestDTO realUser;

    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(RESERVATION_PLACE_SERVICE)).thenReturn(reservationPlaceService);
        when(servletContext.getAttribute(FREE_RESERVATION_SLOT_SERVICE)).thenReturn(freeReservationSlotService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        reservationPlaceDateSlotServlet = new ReservationPlaceDateSlotServlet();
        reservationPlaceDateSlotServlet.init(servletConfig);
        realUser = new UserRequestDTO("root", "root");
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и все даты для бронирования места")
    void dateSlotTest_should_GetAvailableDates_By_ID() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationPlaceService.getReservationPlace(1)).thenReturn(new Workplace());

        reservationPlaceDateSlotServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, когда места с таким id не существует")
    void dateSlotTest_should_Not_GetAvailableDates_By_ID() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationPlaceService.getReservationPlace(1)).thenReturn(null);

        reservationPlaceDateSlotServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, когда id невалиден")
    void dateSlotTest_should_Not_GetAvailableDates_By_NotValid_ID() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1ds");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationPlaceDateSlotServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 401, если пользователь не авторизован")
    void dateSlotTest_should_Return_Code_401_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1ds");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceDateSlotServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и все даты для бронирования")
    void dateSlotTest_should_Not_GetAvailableDates_By_AllPlaces() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationPlaceDateSlotServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(reservationPlaceService, times(1)).getAllReservationPlaces();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 401, если пользователь не авторизован")
    void dateSlotTest_should_Return_Code_401_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceDateSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 404, если нет мест для брони на указанную дату")
    void dateSlotTest_should_Return_Code_404_POST() throws IOException {
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.now());
        when(request.getSession()).thenReturn(session);
        when(objectMapper.readValue(request.getReader(), LocalDateRequest.class)).thenReturn(localDateRequest);
        when(freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate())).thenReturn(new ArrayList<>());
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(response.getWriter()).thenReturn(writer);

        reservationPlaceDateSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 200, если есть места для брони на указанную дату")
    void dateSlotTest_should_Return_Code_200_POST() throws IOException {
        List<ReservationPlace> list = new ArrayList<>();
        list.add(new Workplace());
        when(response.getWriter()).thenReturn(writer);
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.now());
        when(request.getSession()).thenReturn(session);
        when(objectMapper.readValue(request.getReader(), LocalDateRequest.class)).thenReturn(localDateRequest);
        when(freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate())).thenReturn(list);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationPlaceDateSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тело не соответствует требованиям")
    void dateSlotTest_should_Return_Code_400_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(objectMapper.readValue(request.getReader(), LocalDateRequest.class)).thenThrow(JsonMappingException.class);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationPlaceDateSlotServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}