package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.mapper.ReservationPlaceMapperImpl;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceFullRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceServletTest {
    private ReservationPlaceServlet reservationPlaceServlet;
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

        reservationPlaceServlet = new ReservationPlaceServlet();
        reservationPlaceServlet.init(servletConfig);
        realUser = new UserRequestDTO("root", "root");
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 401, если пользователь не авторизован")
    void reservationPlaceTest_should_Return_Code_401_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и вернуть тело со всеми местами")
    void reservationPlaceTest_should_Return_Code_200_GET_All_ReservationPlaces() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationPlaceServlet.doGet(request, response);

        verify(reservationPlaceService, times(1)).getAllReservationPlaces();
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 400 когда идентификатор места не валидный")
    void reservationPlaceTest_should_Return_Code_400_GET_When_ID_IsNotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432dsa");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationPlaceServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, если места с таким id не существует")
    void reservationPlaceTest_should_Return_Code_404_GET_WhenPlaceDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationPlaceService.getReservationPlace(432)).thenReturn(null);

        reservationPlaceServlet.doGet(request, response);

        verify(reservationPlaceService).getReservationPlace(432);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200, если место с таким id существует")
    void reservationPlaceTest_should_Return_Code_200_GET_WhenPlaceExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationPlaceService.getReservationPlace(432)).thenReturn(new Workplace());

        reservationPlaceServlet.doGet(request, response);

        verify(reservationPlaceService).getReservationPlace(432);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 401, если пользователь не авторизован")
    void reservationPlaceTest_should_Return_Code_401_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 201, и сохранить место для бронирования")
    void reservationPlaceTest_should_Return_Code_201_POST() throws IOException {
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = ReservationPlaceRequestDTO.builder()
                .typeId(1)
                .name("32")
                .placeArea(2)
                .numberOfSeats(1)
                .costPerHour(20)
                .build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(objectMapper.readValue(request.getReader(), ReservationPlaceRequestDTO.class)).thenReturn(reservationPlaceRequestDTO);
        when(response.getWriter()).thenReturn(writer);

        reservationPlaceServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если объект в теле не валиден")
    void reservationPlaceTest_should_Return_Code_400_POST_WhenObjectIsNotValid() throws IOException {
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = ReservationPlaceRequestDTO.builder()
                .typeId(0)
                .name("32")
                .placeArea(0)
                .numberOfSeats(0)
                .costPerHour(0)
                .build();
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(objectMapper.readValue(request.getReader(), ReservationPlaceRequestDTO.class)).thenReturn(reservationPlaceRequestDTO);

        reservationPlaceServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 403, если пользователь не является администратором")
    void reservationPlaceTest_should_Return_Code_403_POST_WhenUserWasNotAdmin() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);

        reservationPlaceServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тело запроса не соответствует требованиям")
    void reservationPlaceTest_should_Return_Code_400_POST_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), ReservationPlaceRequestDTO.class)).thenThrow(JsonMappingException.class);

        reservationPlaceServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 401, если пользователь не авторизован")
    void reservationPlaceTest_should_Return_Code_401_PUT() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationPlaceServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 400, если объект в теле не валиден")
    void reservationPlaceTest_should_Return_Code_400_PUT_WhenObjectIsNotValid() throws IOException {
        ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO = ReservationPlaceFullRequestDTO.builder()
                .typeId(0)
                .name("32")
                .placeArea(0)
                .numberOfSeats(0)
                .costPerHour(0)
                .build();
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(objectMapper.readValue(request.getReader(), ReservationPlaceFullRequestDTO.class)).thenReturn(reservationPlaceFullRequestDTO);

        reservationPlaceServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 403, если пользователь не является администратором")
    void reservationPlaceTest_should_Return_Code_403_PUT_WhenUserWasNotAdmin() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new UserRequestDTO());
        when(response.getWriter()).thenReturn(writer);

        reservationPlaceServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 400, если тело запроса не соответствует требованиям")
    void reservationPlaceTest_should_Return_Code_400_PUT_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), ReservationPlaceFullRequestDTO.class)).thenThrow(JsonMappingException.class);

        reservationPlaceServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 201, и обновить конференц зал")
    void reservationPlaceTest_should_Return_Code_201_PUT_ConferenceRoom() throws IOException {
        ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO = ReservationPlaceFullRequestDTO.builder()
                .id(2)
                .typeId(1)
                .name("32")
                .placeArea(2)
                .numberOfSeats(1)
                .costPerHour(20)
                .build();
        ReservationPlaceMapper reservationPlaceMapper = new ReservationPlaceMapperImpl();
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(objectMapper.readValue(request.getReader(), ReservationPlaceFullRequestDTO.class)).thenReturn(reservationPlaceFullRequestDTO);

        reservationPlaceServlet.doPut(request, response);

        verify(reservationPlaceService).updateReservationPlace(reservationPlaceMapper.toConferenceRoom(reservationPlaceFullRequestDTO));
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 201, и обновить рабочее место")
    void reservationPlaceTest_should_Return_Code_201_PUT_Workplace() throws IOException {
        ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO = ReservationPlaceFullRequestDTO.builder()
                .id(2)
                .typeId(2)
                .name("32")
                .placeArea(2)
                .numberOfSeats(1)
                .costPerHour(20)
                .build();
        ReservationPlaceMapper reservationPlaceMapper = new ReservationPlaceMapperImpl();
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute(SESSION_USER)).thenReturn(new AdminRequestDTO());
        when(objectMapper.readValue(request.getReader(), ReservationPlaceFullRequestDTO.class)).thenReturn(reservationPlaceFullRequestDTO);

        reservationPlaceServlet.doPut(request, response);

        verify(reservationPlaceService).updateReservationPlace(reservationPlaceMapper.toConferenceRoom(reservationPlaceFullRequestDTO));
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}