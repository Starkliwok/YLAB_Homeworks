package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Сервлет для авторизации пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class AuthorizationServlet {

//    private final UserMapper userMapper;
//
//    private final ValidatorDTO<UserRequestDTO> validatorDTO;

    private final UserService userService;

    @PostMapping("/login")
    protected void signIn(UserRequestDTO userRequestDTO,HttpServletRequest req) throws IOException {
//        UserRequestDTO userRequestDTO;
//        User user;
//        resp.setContentType(CONTENT_JSON);
//        resp.setCharacterEncoding(ENCODING);
//        try {
//            userRequestDTO = objectMapper.readValue(req.getReader(), UserRequestDTO.class);
//            validatorDTO.validate(userRequestDTO);
//        } catch (JsonMappingException e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write(objectMapper.writeValueAsString(
//                    new ErrorResponse("Тело запроса не соответствует требованиям")));
//            return;
//        } catch (FieldNotValidException e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
//            return;
//        }
//        user = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
//        UserRequestDTO currentUser = (UserRequestDTO) req.getSession().getAttribute(SESSION_USER);
//        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(userRequestDTO);
//        if (currentUser != null) {
//            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            resp.getWriter().write(objectMapper.writeValueAsString(
//                    new ErrorResponse("Вы уже авторизованы как " + currentUser.getUsername())));
//        } else if (user != null) {
//            if(user instanceof Admin) {
//                AdminRequestDTO adminRequestDTO = userMapper.toAdminRequestDTO(userRequestDTO);
//                req.getSession().setAttribute(SESSION_USER, adminRequestDTO);
//            } else {
//                req.getSession().setAttribute(SESSION_USER, userRequestDTO);
//            }
//            resp.setStatus(HttpServletResponse.SC_OK);
//            resp.getWriter().write(objectMapper.writeValueAsString(userResponseDTO));
//        } else {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            resp.getWriter().write(objectMapper.writeValueAsString(
//                    new ErrorResponse("Пользователя с таким логином и паролем не существует")));
//        }
    }
}