package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.ADMIN_ROLE;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.USER_ROLE;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_LOGIN_PATH;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_REGISTRATION_PATH;

/**
 * REST контроллер для авторизации пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@Tag(name = "Взаимодействие с аккаунтом")
public class AuthorizationController {


    /** Поле сервиса пользователей*/
    private final UserService userService;

    /** Поле для валидации DTO объектов пользователей*/
    private final ValidatorDTO<UserRequestDTO> validatorDTO;

    /** Поле для валидации входящих токенов авторизации*/
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthorizationController(UserService userService,
                                   @Qualifier("userReq") ValidatorDTO<UserRequestDTO> validatorDTO,
                                   JwtUtil jwtUtil) {
        this.userService = userService;
        this.validatorDTO = validatorDTO;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Авторизация
     * @param userRequestDTO DTO Объект пользователя содержащий данные для авторизации
     * @return DTO response объект вошедшего в аккаунт пользователя/администратора
     * @throws ObjectNotFoundException Если пользователя с таким логином и паролем не существует
     * @throws FieldNotValidException Если тело запроса не валидно
     */
    @Operation(summary = "Авторизация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Авторизация прошла успешно"),
            @ApiResponse(responseCode = "404", description = "Пользователя с таким логином и паролем не существует")
    })
    @PostMapping(CONTROLLER_LOGIN_PATH)
    public ResponseEntity<?> signIn(@RequestBody UserRequestDTO userRequestDTO) throws FieldNotValidException, ObjectNotFoundException {
        validatorDTO.validate(userRequestDTO);
        User user = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        if (user == null) {
            throw new ObjectNotFoundException("User with this username or password does not exists");
        } else {
            String role = USER_ROLE;
            if (user instanceof Admin) {
                role = ADMIN_ROLE;
            }
            String token = jwtUtil.generateToken(user.getUsername(), role);
            return ResponseEntity.ok().header("Authorization", "Bearer " + token).build();
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