package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.UserService;
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
 * Сервлет панели администратора для взаимодействия с аккаунтами пользователей
 * @author Денис Попов
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_USER_PATH)
public class UserAdminServlet {

    private final UserMapper userMapper;

    private final UserService userService;

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
