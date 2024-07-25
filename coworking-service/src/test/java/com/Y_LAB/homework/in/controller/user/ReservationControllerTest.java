package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ValidatorDTO<ReservationRequestDTO> validatorPostDTO;

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationPlaceService reservationPlaceService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private final String authHeader = "Bearer fdsfsf25435gfdgdfg543543sefse543";

    private final UserRequestDTO userRequestDTO = new UserRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
        userRequestDTO.setPassword("password");
        userRequestDTO.setUsername("username");
    }

    @Test
    @DisplayName("Получение брони пользователя, код 404 когда брони не существует")
    void getUserReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        long reservationId = 1;
        User user = new User(1, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(reservationService.getReservation(reservationId)).thenReturn(null);
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Reservation with id " + reservationId + " does not exists"));

        verify(reservationService, times(1)).getReservation(reservationId);
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Получение брони пользователя, код 200")
    void getUserReservation() throws Exception {
        Reservation mockReservation = new Reservation();
        session.setAttribute(SESSION_USER, userRequestDTO);
        long reservationId = 1L;
        long userId = 1;
        mockReservation.setUserId(userId);
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(reservationService.getReservation(reservationId)).thenReturn(mockReservation);
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session))
                .andExpect(status().isOk());

        verify(reservationMapper, times(1)).toResponseDTO(mockReservation);
        verify(reservationService, times(1)).getReservation(reservationId);
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(reservationMapper);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Получение брони пользователя, код 401 когда пользователь не авторизован")
    void getUserReservation_UnAuthorized() throws Exception {
        mockMvc.perform(get( CONTROLLER_RESERVATION_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех броней пользователя, код 200")
    void getAllUserReservations() throws Exception {
        List<Reservation> mockReservations = new ArrayList<>();
        session.setAttribute(SESSION_USER, userRequestDTO);
        long userId = 1;
        when(reservationService.getAllReservations()).thenReturn(mockReservations);
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PATH).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationService, times(1)).getAllUserReservations(userId);
        verify(reservationMapper, times(1)).toResponseDTOList(mockReservations);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(reservationMapper);
    }

    @Test
    @DisplayName("Получение всех броней пользователя, код 401 когда пользователь не авторизован")
    void getAllUserReservations_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_RESERVATION_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Сохранение брони пользователя, код 401 когда пользователь не авторизован")
    void saveUserReservation_UnAuthorized() throws Exception {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        mockMvc.perform(post(CONTROLLER_RESERVATION_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Сохранение брони пользователя, код 200")
    void saveUserReservation() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        long userId = 1;
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);

        mockMvc.perform(post(CONTROLLER_RESERVATION_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(validatorPostDTO, times(1)).validate(eq(reservationRequestDTO));
        verify(reservationService, times(1)).saveReservation(reservationRequestDTO, userId);
        verifyNoMoreInteractions(validatorPostDTO);
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    @DisplayName("Обновление брони пользователя, код 401 когда пользователь не авторизован")
    void updateUserReservation_UnAuthorized() throws Exception {
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();
        int reservationId = 1;

        mockMvc.perform(put(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Обновление брони пользователя, код 404 когда брони не существует")
    void updateUserReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        int reservationId = 1;
        long userId = 1;
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(null);

        mockMvc.perform(put(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Reservation with id " + reservationId + " does not exists"));

        verify(validatorPostDTO, times(1)).validate(eq(reservationRequestDTO));
        verify(reservationService, times(1)).getReservation(reservationId);
        verifyNoMoreInteractions(validatorPostDTO);
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    @DisplayName("Обновление брони пользователя, код 200")
    void updateUserReservation() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        Reservation reservation = new Reservation();
        ReservationPlace reservationPlace = new ConferenceRoom();
        int reservationId = 1;
        long userId = 1;
        reservation.setUserId(userId);
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(reservationPlaceService.getReservationPlace(reservationRequestDTO.getReservationPlaceId())).thenReturn(reservationPlace);
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(reservationMapper.toReservation(reservationRequestDTO, reservationId, userId, reservationPlace)).thenReturn(reservation);

        mockMvc.perform(put(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        verify(validatorPostDTO, times(1)).validate(eq(reservationRequestDTO));
        verify(reservationService, times(1)).getReservation(reservationId);
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verify(reservationPlaceService, times(1)).getReservationPlace(reservationRequestDTO.getReservationPlaceId());
        verify(reservationService, times(1)).updateReservation(reservation);
        verifyNoMoreInteractions(validatorPostDTO);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Удаление брони пользователя, код 404 когда брони не существует")
    void deleteReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        long reservationId = 1;
        when(reservationService.getReservation(reservationId)).thenReturn(null);
        long userId = 1;
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);

        mockMvc.perform(delete(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Reservation with id " + reservationId + " does not exists"));

        verify(reservationService, times(1)).getReservation(reservationId);
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Удаление брони пользователя, код 200")
    void deleteReservation() throws Exception {
        Reservation mockReservation = new Reservation();
        session.setAttribute(SESSION_USER, userRequestDTO);
        long reservationId = 1L;
        long userId = 1;
        mockReservation.setUserId(userId);
        User user = new User(userId, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword())).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(mockReservation);

        mockMvc.perform(delete(CONTROLLER_RESERVATION_PATH + "/{id}", reservationId).session(session))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).getReservation(reservationId);
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verify(reservationService, times(1)).deleteReservation(reservationId);
        verifyNoMoreInteractions(reservationService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Удаление брони пользователя, код 401 когда пользователь не авторизован")
    void deleteReservation_UnAuthorized() throws Exception {
        mockMvc.perform(delete(CONTROLLER_RESERVATION_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("You are not logged in"));
    }
}