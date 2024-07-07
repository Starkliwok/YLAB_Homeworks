package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.mapper.ReservationPlaceMapperImpl;
import com.Y_LAB.homework.mapper.UserMapperImpl;
import com.Y_LAB.homework.model.dto.request.*;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServletTest {

    private ReservationServlet reservationServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    @Mock
    private ReservationService reservationService;
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
    private User user;

    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(RESERVATION_SERVICE)).thenReturn(reservationService);
        when(servletContext.getAttribute(RESERVATION_PLACE_SERVICE)).thenReturn(reservationPlaceService);
        when(servletContext.getAttribute(USER_SERVICE)).thenReturn(userService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        reservationServlet = new ReservationServlet();
        reservationServlet.init(servletConfig);
        user = new User(1, "someuser", "user");
        realUser = new UserRequestDTO("root", "root");
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 401, если пользователь не авторизован")
    void reservationTest_should_Return_Code_401_GET() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200 и вернуть тело ответа со всеми бронями пользователя")
    void reservationTest_should_Return_Code_200_GET_All_Reservations() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn(null);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doGet(request, response);

        verify(reservationService, times(1)).getAllUserReservations(user.getId());
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 400 когда идентификатор брони не валидный")
    void reservationTest_should_Return_Code_400_GET_When_ID_IsNotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432dsa");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doGet(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, если брони с таким id не существует")
    void reservationTest_should_Return_Code_404_GET_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(432)).thenReturn(null);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doGet(request, response);

        verify(reservationService).getReservation(432);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 404, если бронь с таким id не принадлежит пользователю")
    void reservationTest_should_Return_Code_404_GET_WhenReservationOwnerIsDifferent() throws IOException {
        Reservation reservation = new Reservation();
        reservation.setUserId(4320L);
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(432)).thenReturn(reservation);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doGet(request, response);

        verify(reservationService).getReservation(432);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод GET должен вернуть код 200, если бронь с таким id существует")
    void reservationTest_should_Return_Code_200_GET_WhenReservationExist() throws IOException {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(ServletPathUtil.getPath(request)).thenReturn("432");
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(432)).thenReturn(reservation);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doGet(request, response);

        verify(reservationService).getReservation(432);
        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 401, если пользователь не авторизован")
    void reservationTest_should_Return_Code_401_POST() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 201, и сохранить бронь")
    void reservationTest_should_Return_Code_201_POST() throws IOException {
        ReservationRequestDTO reservationRequestDTO = ReservationRequestDTO.builder()
                .reservationPlaceId(1)
                .startDate(LocalDateTime.of(2024, 10, 1, 1, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 2, 0))
                .build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), ReservationRequestDTO.class)).thenReturn(reservationRequestDTO);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если объект в теле не валиден")
    void reservationTest_should_Return_Code_400_POST_WhenObjectIsNotValid() throws IOException {
        ReservationRequestDTO reservationRequestDTO = ReservationRequestDTO.builder()
                .reservationPlaceId(1)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), ReservationRequestDTO.class)).thenReturn(reservationRequestDTO);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод POST должен вернуть код 400, если тело запроса не соответствует требованиям")
    void reservationTest_should_Return_Code_400_POST_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), ReservationRequestDTO.class)).thenThrow(JsonMappingException.class);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 401, если пользователь не авторизован")
    void reservationTest_should_Return_Code_401_PUT() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(null);

        reservationServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 400, если объект в теле не валиден")
    void reservationTest_should_Return_Code_400_PUT_WhenObjectIsNotValid() throws IOException {
        ReservationPutRequestDTO reservationPutRequestDTO = ReservationPutRequestDTO.builder()
                .id(1)
                .reservationPlaceId(2)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), ReservationPutRequestDTO.class)).thenReturn(reservationPutRequestDTO);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 400, если тело запроса не соответствует требованиям")
    void reservationTest_should_Return_Code_400_PUT_WhenBodyIsNotValid() throws IOException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.readValue(request.getReader(), ReservationPutRequestDTO.class)).thenThrow(JsonMappingException.class);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 200, обновить бронь")
    void reservationTest_should_Return_Code_200_PUT_Reservation() throws IOException {
        ReservationPutRequestDTO reservationPutRequestDTO = ReservationPutRequestDTO.builder()
                .id(1L)
                .reservationPlaceId(2)
                .startDate(LocalDateTime.of(2024, 10, 1, 1, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 2, 0))
                .build();
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), ReservationPutRequestDTO.class)).thenReturn(reservationPutRequestDTO);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);
        when(reservationService.getReservation(reservationPutRequestDTO.getId())).thenReturn(reservation);

        reservationServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 404, если у брони с указанным id другой пользователь")
    void reservationTest_should_Return_Code_404_PUT_WhenReservationOwnerIsDifferent() throws IOException {
        ReservationPutRequestDTO reservationPutRequestDTO = ReservationPutRequestDTO.builder()
                .id(1L)
                .reservationPlaceId(2)
                .startDate(LocalDateTime.of(2024, 10, 1, 1, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 2, 0))
                .build();
        Reservation reservation = new Reservation();
        reservation.setUserId(765763L);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), ReservationPutRequestDTO.class)).thenReturn(reservationPutRequestDTO);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);
        when(reservationService.getReservation(reservationPutRequestDTO.getId())).thenReturn(reservation);
        when(response.getWriter()).thenReturn(writer);

        reservationServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод PUT должен вернуть код 404, если у брони с указанным id другой пользователь")
    void reservationTest_should_Return_Code_404_PUT_WhenReservationDoesNotExist() throws IOException {
        ReservationPutRequestDTO reservationPutRequestDTO = ReservationPutRequestDTO.builder()
                .id(1L)
                .reservationPlaceId(2)
                .startDate(LocalDateTime.of(2024, 10, 1, 1, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 2, 0))
                .build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(objectMapper.readValue(request.getReader(), ReservationPutRequestDTO.class)).thenReturn(reservationPutRequestDTO);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);
        when(reservationService.getReservation(reservationPutRequestDTO.getId())).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        reservationServlet.doPut(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда места с таким id не существует")
    void reservationTest_should_Return_Code_404_DELETE_WhenReservationDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда место с таким id не принадлежит пользователю")
    void reservationTest_should_Return_Code_404_DELETE_Reservation() throws IOException {
        Reservation reservation = new Reservation();
        reservation.setUserId(6546543L);
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(1)).thenReturn(reservation);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);

        reservationServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 404, когда id невалиден")
    void reservationTest_should_Return_Code_404_DELETE_When_ID_NotValid() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(ServletPathUtil.getPath(request)).thenReturn("1ds");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        reservationServlet.doDelete(request, response);

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

        reservationServlet.doDelete(request, response);

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

        reservationServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Метод DELETE должен вернуть код 200 и удалить бронь")
    void reservationTest_should_Return_Code_200_DELETE_Reservation() throws IOException {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        when(ServletPathUtil.getPath(request)).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);
        when(reservationService.getReservation(1)).thenReturn(reservation);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(user);

        reservationServlet.doDelete(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}