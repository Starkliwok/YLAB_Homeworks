package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.SortPlaceTypeRequest;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.Workplace;
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

import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceSortAdminServletTest {
    private ReservationPlaceSortAdminServlet reservationPlaceSortAdminServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ReservationPlaceService reservationPlaceService;
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
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        reservationPlaceSortAdminServlet = new ReservationPlaceSortAdminServlet();
        reservationPlaceSortAdminServlet.init(servletConfig);
        realUser = new AdminRequestDTO();
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 401, если пользователь не авторизован")
    void reservationPlaceSortTest_should_Return_Code_401_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 403, если пользователь не является администратором")
    void reservationPlaceSortTest_should_Return_Code_403_POST_WhenUserWasNotAdmin() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тело запроса не соответствует требованиям")
    void reservationPlaceSortTest_should_Return_Code_400_POST_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), SortPlaceTypeRequest.class)).thenThrow(JsonMappingException.class);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тип равен null")
    void reservationPlaceSortTest_should_Return_Code_400_POST_WhenTypeIsNotValid() throws IOException {
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setPlaceType("Workplace");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortPlaceTypeRequest.class)).thenReturn(sortPlaceTypeRequest);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тип сортировки равен null")
    void reservationPlaceSortTest_should_Return_Code_400_POST_WhenSortTypeIsNotValid() throws IOException {
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortPlaceTypeRequest.class)).thenReturn(sortPlaceTypeRequest);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 200, и вернуть все рабочие места")
    void reservationPlaceSortTest_should_Return_Code_200_POST_SortByWorkplace() throws IOException {
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setPlaceType("Workplace");
        sortPlaceTypeRequest.setSortType("type");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortPlaceTypeRequest.class)).thenReturn(sortPlaceTypeRequest);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(reservationPlaceService).getAllReservationPlacesByTypes(new Workplace());
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 200, и вернуть все конференц-залы")
    void reservationPlaceSortTest_should_Return_Code_200_POST_SortByConferenceRoom() throws IOException {
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setPlaceType("Conference room");
        sortPlaceTypeRequest.setSortType("type");
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), SortPlaceTypeRequest.class)).thenReturn(sortPlaceTypeRequest);

        reservationPlaceSortAdminServlet.doPost(request, response);

        verify(reservationPlaceService).getAllReservationPlacesByTypes(new ConferenceRoom());
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}