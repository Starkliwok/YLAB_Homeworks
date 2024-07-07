package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.mapper.UserMapperImpl;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.OBJECT_MAPPER;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.USER_SERVICE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServletTest {
    private RegistrationServlet registrationServlet;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
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
    private UserRequestDTO newUser;

    private UserRequestDTO realUser;

    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(USER_SERVICE)).thenReturn(userService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        registrationServlet = new RegistrationServlet();
        registrationServlet.init(servletConfig);
        newUser = new UserRequestDTO("rootooojhg", "rootosjhg");
        realUser = new UserRequestDTO("root", "root");
    }

    @Test
    @DisplayName("Проверка на успешную регистрацию")
    void registerTest_should_RegisterUser() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(newUser.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(newUser);

        registrationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response.getWriter()).write(objectMapper.writeValueAsString(newUser.getUsername()));
    }

    @Test
    @DisplayName("Проверка на неуспешную регистрацию, когда пользователь с таким именем уже существует")
    void registerTest_should_Not_RegisterUser_WhenUserWithThisUsernameAlready_Exist() throws IOException, UserAlreadyExistsException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(realUser.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(realUser);
        doThrow(UserAlreadyExistsException.class).when(userService).saveUser(realUser.getUsername(), realUser.getPassword());

        registrationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    @DisplayName("Тело запроса должно соответствовать требованиям")
    void registerTest_should_Not_AuthorizeUser_WithBadBody() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenThrow(JsonMappingException.class);

        registrationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(objectMapper.writeValueAsString("Тело запроса не соответствует требованиям"));
    }

    @Test
    @DisplayName("Тело запроса должно быть валидным")
    void registerTest_should_Not_AuthorizeUser_WhenBodyNotValid() throws IOException {
        UserRequestDTO userRequestDTO = new UserRequestDTO("ro", "ro");
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(userRequestDTO.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(userRequestDTO);

        registrationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}