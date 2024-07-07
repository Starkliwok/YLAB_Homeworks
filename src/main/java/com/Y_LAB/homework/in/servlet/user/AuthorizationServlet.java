package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.mapper.UserMapperImpl;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import com.Y_LAB.homework.validation.impl.UserRequestDTOValidator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_LOGIN_PATH;

/**
 * Сервлет для обработки запросов аутентификации пользователей.
 */
@WebServlet(CONTROLLER_LOGIN_PATH)
public class AuthorizationServlet extends HttpServlet {

    private ObjectMapper objectMapper;

    private final UserMapper userMapper;

    private final ValidatorDTO<UserRequestDTO> validatorDTO;

    private UserService userService;

    public AuthorizationServlet() {
        userMapper = new UserMapperImpl();
        validatorDTO = UserRequestDTOValidator.getInstance();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = (UserService) getServletContext().getAttribute(USER_SERVICE);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    /**
     * @param req  запрос на авторизацию пользователя UserLogInAuthDto
     * @param resp ответ пользователю статус 200 и удаление сессии у пользователя
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequestDTO userRequestDTO;
        User user;
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            userRequestDTO = objectMapper.readValue(req.getReader(), UserRequestDTO.class);
            validatorDTO.validate(userRequestDTO);
        } catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Тело запроса не соответствует требованиям")));
            return;
        } catch (FieldNotValidException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
            return;
        }
        user = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        UserRequestDTO currentUser = (UserRequestDTO) req.getSession().getAttribute(SESSION_USER);
        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(userRequestDTO);
        if (currentUser != null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Вы уже авторизованы как " + currentUser.getUsername())));
        } else if (user != null) {
            if(user instanceof Admin) {
                AdminRequestDTO adminRequestDTO = userMapper.toAdminRequestDTO(userRequestDTO);
                req.getSession().setAttribute(SESSION_USER, adminRequestDTO);
            } else {
                req.getSession().setAttribute(SESSION_USER, userRequestDTO);
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(userResponseDTO));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Пользователя с таким логином и паролем не существует")));
        }
    }
}