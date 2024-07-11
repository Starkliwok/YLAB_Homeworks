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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * Сервлет для авторизации пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class AuthorizationController {

    private final UserMapper userMapper;

    private final UserService userService;

    private final ValidatorDTO<UserRequestDTO> validatorDTO;

    @PostMapping(CONTROLLER_LOGIN_PATH)
    public ResponseEntity<?> signIn(@RequestBody UserRequestDTO userRequestDTO, HttpServletRequest req) throws FieldNotValidException, ObjectNotFoundException {
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

    @GetMapping(CONTROLLER_LOGOUT_PATH)
    public ResponseEntity<MessageResponse> signOut(HttpServletRequest request) throws AuthorizeException {
        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_USER) == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            session.invalidate();
            return ResponseEntity.ok(new MessageResponse("Success"));
        }
    }

    @PostMapping(CONTROLLER_REGISTRATION_PATH)
    public ResponseEntity<MessageResponse> signUp(@RequestBody UserRequestDTO userRequestDTO) throws UserAlreadyExistsException, FieldNotValidException {
        validatorDTO.validate(userRequestDTO);
        userService.saveUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
        return ResponseEntity.ok(new MessageResponse("Success, your login - " + userRequestDTO.getUsername()));
    }
}