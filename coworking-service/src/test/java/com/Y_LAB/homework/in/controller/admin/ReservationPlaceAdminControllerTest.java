package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.SortPlaceTypeRequest;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.validation.impl.ReservationPlaceRequestDTOValidator;
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
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationPlaceAdminControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ReservationPlaceAdminController reservationPlaceAdminController;

    @Mock
    private ReservationPlaceService reservationPlaceService;

    @Mock
    private ReservationPlaceMapper reservationPlaceMapper;

    @Mock
    private ReservationPlaceRequestDTOValidator serviceA;

    @Mock
    private JwtUtil jwtUtil;

    private final String authHeader = "Bearer fdsfsf25435gfdgdfg543543sefse543";

    private final AdminRequestDTO adminRequestDTO = new AdminRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationPlaceAdminController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Получение всех конференц залов, код 200")
    void getReservationPlacesSortedByType_ConferenceRoom() throws Exception {
        List<ReservationPlace> mockReservationPlaces = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type");
        sortPlaceTypeRequest.setPlaceType("conference room");
        when(reservationPlaceService.getAllReservationPlacesByTypes(new ConferenceRoom())).thenReturn(mockReservationPlaces);

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortPlaceTypeRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationPlaceService, times(1)).getAllReservationPlacesByTypes(new ConferenceRoom());
        verify(reservationPlaceMapper, times(1)).toResponseDTOList(mockReservationPlaces);
        verifyNoMoreInteractions(reservationPlaceService);
        verifyNoMoreInteractions(reservationPlaceMapper);
    }

    @Test
    @DisplayName("Получение всех рабочих мест, код 200")
    void getReservationPlacesSortedByType_Workplace() throws Exception {
        List<ReservationPlace> mockReservationPlaces = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type");
        sortPlaceTypeRequest.setPlaceType("workplace");
        when(reservationPlaceService.getAllReservationPlacesByTypes(new Workplace())).thenReturn(mockReservationPlaces);

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortPlaceTypeRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationPlaceService, times(1)).getAllReservationPlacesByTypes(new Workplace());
        verify(reservationPlaceMapper, times(1)).toResponseDTOList(mockReservationPlaces);
        verifyNoMoreInteractions(reservationPlaceService);
        verifyNoMoreInteractions(reservationPlaceMapper);
    }

    @Test
    @DisplayName("Получение всех отсортированных мест, код 403 когда пользователь не является администратором")
    void getReservationPlacesSortedByType_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type");
        sortPlaceTypeRequest.setPlaceType("workplace");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortPlaceTypeRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение всех отсортированных мест, код 401 когда пользователь не авторизован")
    void getReservationPlacesSortedByType_UnAuthorized() throws Exception {
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type");
        sortPlaceTypeRequest.setPlaceType("workplace");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortPlaceTypeRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех отсортированных мест, код 400 когда указан несуществующий тип сортировки")
    void getReservationPlacesSortedByType_BadSortType() throws Exception {
        session.setAttribute(SESSION_USER, new AdminRequestDTO());
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type5432");
        sortPlaceTypeRequest.setPlaceType("workplace");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortPlaceTypeRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Получение всех отсортированных мест, код 400 когда указан несуществующее место для сортировки по типу")
    void getReservationPlacesSortedByType_BadPlaceType() throws Exception {
        session.setAttribute(SESSION_USER, new AdminRequestDTO());
        SortPlaceTypeRequest sortPlaceTypeRequest = new SortPlaceTypeRequest();
        sortPlaceTypeRequest.setSortType("type");
        sortPlaceTypeRequest.setPlaceType("workplace54353");

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortPlaceTypeRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Сохранение места для бронирования, код 403 когда пользователь не является администратором")
    void saveReservationPlace_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Сохранение места для бронирования, код 401 когда пользователь не авторизован")
    void saveReservationPlace_UnAuthorized() throws Exception {
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH)
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
    @DisplayName("Сохранение места для бронирования, код 200")
    void saveReservationPlace() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();

        mockMvc.perform(post(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(serviceA, times(1)).validate(eq(reservationPlaceRequestDTO));
        verify(reservationPlaceService, times(1)).saveReservationPlace(reservationPlaceRequestDTO);
        verifyNoMoreInteractions(serviceA);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Обновление места для бронирования, код 403 когда пользователь не является администратором")
    void updateReservationPlace_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();
        int reservationPlaceId = 1;

        mockMvc.perform(put(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Обновление места для бронирования, код 401 когда пользователь не авторизован")
    void updateReservationPlace_UnAuthorized() throws Exception {
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();
        int reservationPlaceId = 1;

        mockMvc.perform(put(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
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
    @DisplayName("Обновление места для бронирования, код 404 когда места не существует")
    void updateReservationPlace_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(null);

        mockMvc.perform(put(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Place with id " + reservationPlaceId + " does not exists"));

        verify(serviceA, times(1)).validate(eq(reservationPlaceRequestDTO));
        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(serviceA);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Обновление конференц зала, код 200")
    void updateReservationPlace_ConferenceRoom() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();
        reservationPlaceRequestDTO.setTypeId(1);
        ConferenceRoom reservationPlace = new ConferenceRoom();
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(reservationPlace);
        when(reservationPlaceMapper.toConferenceRoom(reservationPlaceRequestDTO)).thenReturn(reservationPlace);

        mockMvc.perform(put(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        verify(reservationPlaceMapper, times(1)).toConferenceRoom(eq(reservationPlaceRequestDTO));
        verify(serviceA, times(1)).validate(eq(reservationPlaceRequestDTO));
        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verify(reservationPlaceService, times(1)).updateReservationPlace(reservationPlace);
        verifyNoMoreInteractions(reservationPlaceMapper);
        verifyNoMoreInteractions(serviceA);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Обновление рабочего места, код 200")
    void updateReservationPlace_Workplace() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        ReservationPlaceRequestDTO reservationPlaceRequestDTO = new ReservationPlaceRequestDTO();
        reservationPlaceRequestDTO.setTypeId(2);
        Workplace reservationPlace = new Workplace();
        int reservationPlaceId = 4;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(reservationPlace);
        when(reservationPlaceMapper.toWorkplace(reservationPlaceRequestDTO)).thenReturn(reservationPlace);

        mockMvc.perform(put(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPlaceRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        verify(reservationPlaceMapper, times(1)).toWorkplace(eq(reservationPlaceRequestDTO));
        verify(serviceA, times(1)).validate(eq(reservationPlaceRequestDTO));
        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verify(reservationPlaceService, times(1)).updateReservationPlace(reservationPlace);
        verifyNoMoreInteractions(reservationPlaceMapper);
        verifyNoMoreInteractions(serviceA);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Удаление рабочего места, код 403 когда пользователь не является администратором")
    void deleteReservationPlace_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        int reservationPlaceId = 1;

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Удаление рабочего места, код 401 когда пользователь не авторизован")
    void deleteReservationPlace_UnAuthorized() throws Exception {
        int reservationPlaceId = 1;

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Удаление рабочего места, код 404 когда брони не существует")
    void deleteReservationPlace_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(null);

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Place with id " + reservationPlaceId + " does not exists"));

        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(reservationPlaceService);
    }

    @Test
    @DisplayName("Удаление рабочего места, код 200")
    void deleteReservationPlace() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        int reservationPlaceId = 1;
        when(reservationPlaceService.getReservationPlace(reservationPlaceId)).thenReturn(new Workplace());

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + "/{id}", reservationPlaceId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reservationPlaceService, times(1)).getReservationPlace(reservationPlaceId);
        verify(reservationPlaceService, times(1)).deleteReservationPlace(reservationPlaceId);
        verifyNoMoreInteractions(reservationPlaceService);
    }
}