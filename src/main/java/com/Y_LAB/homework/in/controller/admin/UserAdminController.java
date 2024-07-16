package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_USER_PATH;

/**
 * REST контроллер панели администратора для взаимодействия с аккаунтами пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH)
@Tag(name = "(ADMIN) Пользователи")
public class UserAdminController {

    /** Поле для преобразования DTO объектов пользователей*/
    private final UserMapper userMapper;

    /** Поле сервиса пользователей*/
    private final UserService userService;

    /**
     * Получение пользователя по id
     * @param userId Уникальный идентификатор пользователя
     * @param req Http запрос для получения сессии пользователя
     * @return Объект DTO Response пользователя
     * @throws ObjectNotFoundException Если пользователя с таким id не существует
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным id не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("id") Long userId,
                                     HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            User user = userService.getUser(userId);
            if (user != null) {
                UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(user);
                return ResponseEntity.ok(userResponseDTO);
            } else {
                throw new ObjectNotFoundException("User with id " + userId + " does not exists");
            }
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех пользователей
     * @param req Http запрос для получения сессии пользователя
     * @return Список с объектами DTO Response пользователей
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех пользователей"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            List<User> users = userService.getAllUsers();
            List<UserResponseDTO> userResponseDTOList = userMapper.toUserResponseDTOList(users);
            return ResponseEntity.ok(userResponseDTOList);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Удаление пользователя по id
     * @param userId Уникальный идентификатор пользователя
     * @param req Http запрос для получения сессии пользователя
     * @return Сообщение об успешном удалении пользователя
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если пользователя с таким id не существует
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Удаление пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким id не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable("id") Long userId,
                              HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        if(userService.getUser(userId) != null) {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new MessageResponse("The user has been deleted"));
        } else {
            throw new ObjectNotFoundException("User with id " + userId + " does not exists");
        }
    }
}
