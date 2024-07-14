package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.config.WebConfig;
import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.SortRequest;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = WebConfig.class)
class ReservationAdminControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ReservationAdminController reservationAdminController;

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationMapper reservationMapper;

    private final AdminRequestDTO adminRequestDTO = new AdminRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(reservationAdminController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Получение брони, код 403 когда пользователь не является администратором")
    void getReservation_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        long reservationId = 1L;

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение брони, код 404 когда брони не существует")
    void getReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long reservationId = 1;
        when(reservationService.getReservation(reservationId)).thenThrow(
                new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists"));

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Reservation with id " + reservationId + " does not exists"));

        verify(reservationService, times(1)).getReservation(reservationId);
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    @DisplayName("Получение брони, код 200")
    void getReservation() throws Exception {
        Reservation mockReservation = new Reservation();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long reservationId = 1L;
        when(reservationService.getReservation(reservationId)).thenReturn(mockReservation);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session))
                .andExpect(status().isOk());

        verify(reservationMapper, times(1)).toResponseDTO(mockReservation);
        verify(reservationService, times(1)).getReservation(reservationId);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(reservationMapper);
    }

    @Test
    @DisplayName("Получение брони, код 401 когда пользователь не авторизован")
    void getReservation_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех броней, код 200")
    void getAllReservations() throws Exception {
        List<Reservation> mockReservations = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        when(reservationService.getAllReservations()).thenReturn(mockReservations);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationService, times(1)).getAllReservations();
        verify(reservationMapper, times(1)).toResponseDTOList(mockReservations);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(reservationMapper);
    }

    @Test
    @DisplayName("Получение всех броней, код 403 когда пользователь не является администратором")
    void getAllReservations_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение всех броней, код 401 когда пользователь не авторизован")
    void getAllReservations_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Удаление брони, код 403 когда пользователь не является администратором")
    void deleteReservation_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        long reservationId = 1L;

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Удаление брони, код 404 когда брони не существует")
    void deleteReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long reservationId = 1;
        doThrow(new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists"))
                .when(reservationService).deleteReservation(reservationId);

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Reservation with id " + reservationId + " does not exists"));
    }

    @Test
    @DisplayName("Удаление брони, код 200")
    void deleteReservation() throws Exception {
        Reservation mockReservation = new Reservation();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long reservationId = 1L;
        when(reservationService.getReservation(reservationId)).thenReturn(mockReservation);

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).deleteReservation(reservationId);
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    @DisplayName("Удаление брони, код 401 когда пользователь не авторизован")
    void deleteReservation_UnAuthorized() throws Exception {
        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех броней отсортированные по пользователям, код 200")
    void getAllReservationsSortedByUsers() throws Exception {
        List<Reservation> mockReservations = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("user");
        when(reservationService.getAllReservations()).thenReturn(mockReservations);

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationService, times(1)).getAllReservationsByUsers();
        verify(reservationMapper, times(1)).toResponseDTOList(mockReservations);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(reservationMapper);
    }

    @Test
    @DisplayName("Получение всех броней отсортированные по дате, код 200")
    void getAllReservationsSortedByDate() throws Exception {
        List<Reservation> mockReservations = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("date");
        when(reservationService.getAllReservations()).thenReturn(mockReservations);

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationService, times(1)).getAllReservationsByDate();
        verify(reservationMapper, times(1)).toResponseDTOList(mockReservations);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(reservationMapper);
    }

    @Test
    @DisplayName("Получение всех отсортированных броней, код 403 когда пользователь не является администратором")
    void getAllReservationsSortedByField_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("user");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение всех отсортированных броней, код 401 когда пользователь не авторизован")
    void getAllReservationsSortedByField_UnAuthorized() throws Exception {
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("user");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех отсортированных броней, код 400 когда указана несуществующая сортировка")
    void getAllReservationsSortedByField_BadSortType() throws Exception {
        session.setAttribute(SESSION_USER, new AdminRequestDTO());
        SortRequest sortRequest = new SortRequest();
        sortRequest.setSortType("user432");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}