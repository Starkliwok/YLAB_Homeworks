package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.request.LocalDateRequest;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ReservationPlaceController reservationPlaceController;

    @Mock
    private ReservationPlaceMapper reservationPlaceMapper;

    @Mock
    private ReservationPlaceService reservationPlaceService;

    @Mock
    private FreeReservationSlotService freeReservationSlotService;

    @Mock
    private JwtUtil jwtUtil;

    private final String authHeader = "Bearer fdsfsf25435gfdgdfg543543sefse543";

    private final UserRequestDTO userRequestDTO = new UserRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationPlaceController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
        userRequestDTO.setPassword("password");
        userRequestDTO.setUsername("username");
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Получение места для бронирования, код 404 когда места не существует")
    void getReservationPlace_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(null);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Place with id " + reservationPlaceId + " does not exists"));

        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Получение места для бронирования, код 200")
    void getReservationPlace() throws Exception {
        ReservationPlace reservationPlace = new ConferenceRoom();
        int reservationPlaceId = 1;
        session.setAttribute(SESSION_USER, userRequestDTO);
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(reservationPlace);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId).session(session))
                .andExpect(status().isOk());

        verify(reservationPlaceMapper, times(1)).toResponseDTO(reservationPlace);
        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(reservationPlaceMapper);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Получение места для бронирования, код 401 когда пользователь не авторизован")
    void getReservationPlace_UnAuthorized() throws Exception {
        mockMvc.perform(get( CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех мест для бронирования, код 200")
    void getAllReservationPlaces() throws Exception {
        List<ReservationPlace> mockReservationPlaces = new ArrayList<>();
        session.setAttribute(SESSION_USER, userRequestDTO);
        when(reservationPlaceService.getAllReservationPlaces()).thenReturn(mockReservationPlaces);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationPlaceService, times(1)).getAllReservationPlaces();
        verify(reservationPlaceMapper, times(1)).toResponseDTOList(mockReservationPlaces);
        verifyNoMoreInteractions(reservationPlaceService);
        verifyNoMoreInteractions(reservationPlaceMapper);
    }

    @Test
    @DisplayName("Получение всех мест для бронирования, код 401 когда пользователь не авторизован")
    void getAllReservationPlaces_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех доступных дат для бронирования места, код 404 когда места не существует")
    void getAvailableDatesForPlace_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(null);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Place with id " + reservationPlaceId + " does not exists"));

        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Получение всех доступных дат для бронирования места, код 200")
    void getAvailableDatesForPlace() throws Exception {
        ReservationPlace mockReservationPlace = new Workplace();
        List<LocalDate> localDates = new ArrayList<>();
        ReservationPlaceDateSlotResponseDTO dto = new ReservationPlaceDateSlotResponseDTO();
        session.setAttribute(SESSION_USER, userRequestDTO);
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(mockReservationPlace);
        when(freeReservationSlotService.getAllAvailableDatesForReservePlace(mockReservationPlace)).thenReturn(localDates);
        when(reservationPlaceMapper.toFreeSlotResponseDTO(mockReservationPlace, localDates)).thenReturn(dto);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId).session(session))
                .andExpect(status().isOk());

        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Получение всех доступных дат для бронирования места, код 401 когда пользователь не авторизован")
    void getAvailableDatesForPlace_UnAuthorized() throws Exception {
        mockMvc.perform(get( CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех доступных дат для бронирования всех мест, код 200")
    void getAllAvailableDatesForPlaces() throws Exception {
        List<ReservationPlace> mockReservationPlaces = new ArrayList<>();
        mockReservationPlaces.add(new Workplace());
        List<LocalDate> localDates = new ArrayList<>();
        ReservationPlaceDateSlotResponseDTO dto = new ReservationPlaceDateSlotResponseDTO();
        session.setAttribute(SESSION_USER, userRequestDTO);
        when(reservationPlaceService.getAllReservationPlaces()).thenReturn(mockReservationPlaces);
        when(freeReservationSlotService.getAllAvailableDatesForReservePlace(mockReservationPlaces.get(0))).thenReturn(localDates);
        when(reservationPlaceMapper.toFreeSlotResponseDTO(mockReservationPlaces.get(0), localDates)).thenReturn(dto);

        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH, mockReservationPlaces).session(session))
                .andExpect(status().isOk());

        verify(reservationPlaceService, times(1)).getAllReservationPlaces();
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Получение всех доступных дат для бронирования всех мест, код 401 когда пользователь не авторизован")
    void getAllAvailableDatesForPlaces_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_RESERVATION_PLACE_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех доступных мест для бронирования на указанную дату, код 401 когда пользователь не авторизован")
    void getAvailablePlaceForReserveByDate_UnAuthorized() throws Exception {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        mockMvc.perform(post(CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_DATE_SLOT_PATH)
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
    @DisplayName("Получение всех доступных мест для бронирования на указанную дату, код 404 когда дат нет")
    void getAvailablePlaceForReserveByDate_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        List<ReservationPlace> availableReservationPlaces = new ArrayList<>();
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.of(2024, 1, 1));
        when(freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate()))
                .thenReturn(availableReservationPlaces);

        mockMvc.perform(post(CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_DATE_SLOT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localDateRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Place for reservation on date 2024-01-01 does not exists"));

        verify(freeReservationSlotService, times(1)).getAllAvailablePlacesForReserveDate(eq(localDateRequest.getLocalDate()));
        verifyNoMoreInteractions(freeReservationSlotService);
    }

    @Test
    @DisplayName("Получение всех доступных мест для бронирования на указанную дату, код 200")
    void getAvailablePlaceForReserveByDate() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        List<ReservationPlace> availableReservationPlaces = new ArrayList<>();
        availableReservationPlaces.add(new Workplace());
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.of(2024, 1, 1));
        when(freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate()))
                .thenReturn(availableReservationPlaces);

        mockMvc.perform(post(CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_DATE_SLOT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localDateRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(freeReservationSlotService, times(1)).getAllAvailablePlacesForReserveDate(eq(localDateRequest.getLocalDate()));
        verifyNoMoreInteractions(freeReservationSlotService);
    }

    @Test
    @DisplayName("Получение всех доступных промежутков времени для бронирования места на указанную дату, код 401 когда пользователь не авторизован")
    void getAvailableTimesForReservation_UnAuthorized() throws Exception {
        int reservationPlaceId = 1;
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.of(2024, 1, 1));

        mockMvc.perform(post(CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_TIME_SLOT_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localDateRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех доступных промежутков времени для бронирования места на указанную дату, код 404 когда места не существует")
    void getAvailableTimesForReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        int reservationPlaceId = 1;
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.of(2024, 1, 1));
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(null);

        mockMvc.perform(post(CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_TIME_SLOT_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localDateRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Place with id " + reservationPlaceId + " does not exists"));
    }

    @Test
    @DisplayName("Получение всех доступных промежутков времени для бронирования места на указанную дату, код 200")
    void getAvailableTimesForReservation() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);
        ReservationPlace reservationPlace = new Workplace();
        int reservationPlaceId = 1;
        LocalDateRequest localDateRequest = new LocalDateRequest();
        localDateRequest.setLocalDate(LocalDate.of(2024, 1, 1));
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(reservationPlace);

        mockMvc.perform(post(CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_TIME_SLOT_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localDateRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(freeReservationSlotService, times(1))
                .getAllAvailableTimeListForReservation(reservationPlace, localDateRequest.getLocalDate());
        verifyNoMoreInteractions(freeReservationSlotService);
    }
}