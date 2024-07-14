package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.AdminResponseDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * REST контроллер для авторизации пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@Tag(name = "Взаимодействие с аккаунтом")
public class AuthorizationController {

    /** Поле для преобразования DTO объектов пользователей*/
    private final UserMapper userMapper;

    /** Поле сервиса пользователей*/
    private final UserService userService;

    /** Поле для валидации DTO объектов пользователей*/
    private final ValidatorDTO<UserRequestDTO> validatorDTO;

    @Autowired
    public AuthorizationController(UserMapper userMapper,
                                   UserService userService,
                                   @Qualifier("userReq") ValidatorDTO<UserRequestDTO> validatorDTO) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.validatorDTO = validatorDTO;
    }

    /**
     * Авторизация
     * @param userRequestDTO DTO Объект пользователя содержащий данные для авторизации
     * @param req Http запрос для получения сессии пользователя
     * @return DTO response объект вошедшего в аккаунт пользователя/администратора
     * @throws ObjectNotFoundException Если пользователя с таким логином и паролем не существует
     * @throws FieldNotValidException Если тело запроса не валидно
     */
    @Operation(summary = "Авторизация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Авторизация прошла успешно"),
            @ApiResponse(responseCode = "403", description = "Пользователь уже авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователя с таким логином и паролем не существует")
    })
    @PostMapping(CONTROLLER_LOGIN_PATH)
    public ResponseEntity<?> signIn
            (@RequestBody UserRequestDTO userRequestDTO,
             HttpServletRequest req) throws FieldNotValidException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO currentUser = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(currentUser != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    "You already signIn with login " + currentUser.getUsername()));
        }
        validatorDTO.validate(userRequestDTO);
        User user = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        if (user == null) {
            throw new ObjectNotFoundException("User with this username or password does not exists");
        } else if (user instanceof Admin) {
            AdminRequestDTO adminRequestDTO = userMapper.toAdminRequestDTO(userRequestDTO);
            session.setAttribute(SESSION_USER, adminRequestDTO);
            AdminResponseDTO adminResponseDTO = userMapper.toAdminResponseDTO(adminRequestDTO);
            return ResponseEntity.ok(adminResponseDTO);
        }
        session.setAttribute(SESSION_USER, userRequestDTO);
        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    /**
     * Выход из аккаунта
     * @param req Http запрос для получения сессии пользователя
     * @return Сообщение об успешном выходе из аккаунта
     * @throws AuthorizeException Если пользователь не авторизован
     */
    @Operation(summary = "Выход из аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход из аккаунта"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping(CONTROLLER_LOGOUT_PATH)
    public ResponseEntity<MessageResponse> signOut(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        if (session.getAttribute(SESSION_USER) == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            session.invalidate();
            return ResponseEntity.ok(new MessageResponse("Success"));
        }
    }

    /**
     * Регистрация нового аккаунта пользователя
     * @param userRequestDTO DTO Объект пользователя содержащий данные для регистрации
     * @return Сообщение об успешной регистрации
     * @throws UserAlreadyExistsException Если пользователь с таким логином уже существует
     * @throws FieldNotValidException Если тело запроса не валидно
     */
    @Operation(summary = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация прошла успешно"),
            @ApiResponse(responseCode = "400", description = "Тело запроса не валидно"),
            @ApiResponse(responseCode = "403", description = "Пользователь с таким логином уже существует")
    })
    @PostMapping(CONTROLLER_REGISTRATION_PATH)
    public ResponseEntity<MessageResponse> signUp
            (@RequestBody UserRequestDTO userRequestDTO) throws UserAlreadyExistsException, FieldNotValidException {
        validatorDTO.validate(userRequestDTO);
        userService.saveUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        return ResponseEntity.ok(new MessageResponse("Success, your login - " + userRequestDTO.getUsername()));
    }
}