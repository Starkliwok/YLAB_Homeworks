package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.config.WebConfig;
import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
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

import java.util.Objects;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = WebConfig.class)
class AuthorizationControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AuthorizationController authorizationController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private ValidatorDTO<UserRequestDTO> validatorDTO;

    private final UserRequestDTO userRequestDTO = new UserRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(authorizationController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
        userRequestDTO.setPassword("password");
        userRequestDTO.setUsername("username");
    }

    @Test
    @DisplayName("Авторизация, код 403 когда пользователь уже авторизован")
    void signIn_AlreadyAuthorized() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);

        mockMvc.perform(post(CONTROLLER_LOGIN_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Вход, код 200")
    void signIn() throws Exception {
        User user = new User(1, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()))
                .thenReturn(user);

        mockMvc.perform(post(CONTROLLER_LOGIN_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        verify(userMapper, times(1)).toUserResponseDTO(userRequestDTO);
        verify(validatorDTO, times(1)).validate(eq(userRequestDTO));
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(validatorDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Вход, код 404 когда пользователя не существует")
    void signIn_UserNotFound() throws Exception {
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()))
                .thenReturn(null);

        mockMvc.perform(post(CONTROLLER_LOGIN_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("User with this username or password does not exists"));

        verify(validatorDTO, times(1)).validate(eq(userRequestDTO));
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(validatorDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Вход как администратор, код 200")
    void signIn_Admin() throws Exception {
        User admin = new Admin(1, userRequestDTO.getUsername(), userRequestDTO.getPassword());
        when(userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()))
                .thenReturn(admin);
        AdminRequestDTO adminRequestDTO = new AdminRequestDTO();
        when(userMapper.toAdminRequestDTO(userRequestDTO)).thenReturn(adminRequestDTO);

        mockMvc.perform(post(CONTROLLER_LOGIN_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        verify(userMapper, times(1)).toAdminRequestDTO(userRequestDTO);
        verify(userMapper, times(1)).toAdminResponseDTO(adminRequestDTO);
        verify(validatorDTO, times(1)).validate(eq(userRequestDTO));
        verify(userService, times(1)).getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(validatorDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Выход из приложения, код 200")
    void signOut() throws Exception {
        session.setAttribute(SESSION_USER, userRequestDTO);

        mockMvc.perform(get(CONTROLLER_LOGOUT_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Выход из приложения, код 401 когда пользователь не авторизован")
    void signOut_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_LOGOUT_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Регистрация, код 403 когда пользователь с таким логином уже существует")
    void signUp_UserAlreadyExists() throws Exception {
        doThrow(UserAlreadyExistsException.class).when(userService).saveUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());

        mockMvc.perform(post(CONTROLLER_REGISTRATION_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(UserAlreadyExistsException.class, result.getResolvedException()));

        verify(validatorDTO, times(1)).validate(eq(userRequestDTO));
        verify(userService, times(1)).saveUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(validatorDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Регистрация, код 400 когда тело запроса не валидно")
    void signUp_NotValid() throws Exception {
        doThrow(FieldNotValidException.class).when(validatorDTO).validate(userRequestDTO);

        mockMvc.perform(post(CONTROLLER_REGISTRATION_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(FieldNotValidException.class, result.getResolvedException()));

        verify(validatorDTO, times(1)).validate(eq(userRequestDTO));
        verifyNoMoreInteractions(validatorDTO);
    }

    @Test
    @DisplayName("Регистрация, код 200")
    void signUp() throws Exception {
        mockMvc.perform(post(CONTROLLER_REGISTRATION_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(validatorDTO, times(1)).validate(eq(userRequestDTO));
        verify(userService, times(1)).saveUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        verifyNoMoreInteractions(validatorDTO);
        verifyNoMoreInteractions(userService);
    }
}