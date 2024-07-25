package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.UserService;
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
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_USER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserAdminControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserAdminController userAdminController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private final String authHeader = "Bearer fdsfsf25435gfdgdfg543543sefse543";

    private final AdminRequestDTO adminRequestDTO = new AdminRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userAdminController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Получение пользователя, код 403 когда пользователь не является администратором")
    void getUser_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        long userId = 1L;

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", userId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение пользователя, код 404 когда пользователя не существует")
    void getUser_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long userId = 1;
        when(userService.getUser(userId)).thenReturn(null);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", userId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("User with id " + userId + " does not exists"));

        verify(userService, times(1)).getUser(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Получение пользователя, код 200")
    void getUser() throws Exception {
        User mockUser = new User(1, "username", "password");
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long userId = 1L;
        when(userService.getUser(userId)).thenReturn(mockUser);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", userId).session(session))
                .andExpect(status().isOk());

        verify(userMapper, times(1)).toUserResponseDTO(mockUser);
        verify(userService, times(1)).getUser(userId);
        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Получение пользователя, код 401 когда пользователь не авторизован")
    void getUser_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех пользователей, код 200")
    void getAllUsers() throws Exception {
        List<User> mockUsers = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userService, times(1)).getAllUsers();
        verify(userMapper, times(1)).toUserResponseDTOList(mockUsers);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("Получение всех пользователей, код 403 когда пользователь не является администратором")
    void getAllUsers_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение всех пользователей, код 401 когда пользователь не авторизован")
    void getAllUsers_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Удаление брони, код 403 когда пользователь не является администратором")
    void deleteReservation_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        long userId = 1;

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", userId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Удаление брони, код 404 когда брони не существует")
    void deleteReservation_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long userId = 1;
        when(userService.getUser(userId)).thenReturn(null);

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", userId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("User with id " + userId + " does not exists"));

        verify(userService, times(1)).getUser(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Удаление брони, код 200")
    void deleteReservation() throws Exception {
        User mockUser = new User(1, "username", "password");
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long userId = 1;

        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", userId).session(session))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUser(userId);
        verify(userService, times(1)).deleteUser(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Удаление брони, код 401 когда пользователь не авторизован")
    void deleteReservation_UnAuthorized() throws Exception {
        mockMvc.perform(delete(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }
}