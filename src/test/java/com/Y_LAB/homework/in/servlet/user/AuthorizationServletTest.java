package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
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

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServletTest {

    private AuthorizationServlet authorizationServlet;
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
    @Mock
    private HttpSession session;
    private UserRequestDTO realUser;

    @BeforeEach
    void setUp() throws ServletException {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(USER_SERVICE)).thenReturn(userService);
        when(servletContext.getAttribute(OBJECT_MAPPER)).thenReturn(objectMapper);

        authorizationServlet = new AuthorizationServlet();
        authorizationServlet.init(servletConfig);
        realUser = new UserRequestDTO("root", "root");
    }

    @Test
    @DisplayName("Проверка на успешную авторизацию")
    void authTest_should_AuthorizeUser() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(realUser.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(realUser);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(new User(1, "root", "root"));
        when(request.getSession()).thenReturn(session);

        authorizationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Проверка на неуспешную авторизацию, когда пользователя не существует")
    void authTest_should_Not_AuthorizeUser_WhenDoesNotExist() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(realUser.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(realUser);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(null);
        when(request.getSession()).thenReturn(session);

        authorizationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Проверка на неуспешную авторизацию, когда пользователь уже авторизован")
    void authTest_should_Not_AuthorizeUser_IfHaveSession() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(realUser.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(realUser);
        when(userService.getUser(realUser.getUsername(), realUser.getPassword())).thenReturn(new User(1, "root", "root"));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_USER)).thenReturn(realUser);

        authorizationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Тело запроса должно соответствовать требованиям")
    void authTest_should_Not_AuthorizeUser_WithBadBody() throws IOException {
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenThrow(JsonMappingException.class);

        authorizationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тело запроса должно быть валидным")
    void authTest_should_Not_AuthorizeUser_WhenBodyNotValid() throws IOException {
        UserRequestDTO userRequestDTO = new UserRequestDTO("ro", "ro");
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(userRequestDTO.toString())));
        when(objectMapper.readValue(request.getReader(), UserRequestDTO.class)).thenReturn(userRequestDTO);

        authorizationServlet.doPost(request, response);

        verify(response).setContentType(CONTENT_JSON);
        verify(response).setCharacterEncoding(ENCODING);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}