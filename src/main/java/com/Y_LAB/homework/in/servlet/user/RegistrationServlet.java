package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
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
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.OBJECT_MAPPER;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.USER_SERVICE;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_REGISTRATION_PATH;

/**
 * Сервлет для регистрации новых пользователей
 * @author Денис Попов
 * @version 1.0
 */
@WebServlet(CONTROLLER_REGISTRATION_PATH)
public class RegistrationServlet extends HttpServlet {

    private ObjectMapper objectMapper;

    private final ValidatorDTO<UserRequestDTO> validatorDTO;

    private UserService userService;

    public RegistrationServlet() {
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
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            userRequestDTO = objectMapper.readValue(req.getReader(), UserRequestDTO.class);
            validatorDTO.validate(userRequestDTO);
            userService.saveUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new MessageResponse("Вы успешно зарегистрировались под логином - " + userRequestDTO.getUsername())));
        } catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Тело запроса не может быть пустым")));
        } catch (UserAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        } catch (FieldNotValidException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        }
    }
}