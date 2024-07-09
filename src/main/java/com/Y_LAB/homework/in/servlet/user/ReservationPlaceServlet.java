package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.mapper.ReservationPlaceMapperImpl;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceFullRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import com.Y_LAB.homework.validation.impl.ReservationPlaceFullRequestDTOValidator;
import com.Y_LAB.homework.validation.impl.ReservationPlaceRequestDTOValidator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PLACE_PATH;

/**
 * Сервлет для взаимодействия с местами для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@WebServlet(urlPatterns = {CONTROLLER_RESERVATION_PLACE_PATH, CONTROLLER_RESERVATION_PLACE_PATH + "/*"})
public class ReservationPlaceServlet extends HttpServlet {
    private final ReservationPlaceMapper reservationPlaceMapper;

    private final ValidatorDTO<ReservationPlaceRequestDTO> validatorPostDTO;

    private final ValidatorDTO<ReservationPlaceFullRequestDTO> validatorPutDTO;

    private ReservationPlaceService reservationPlaceService;

    private ObjectMapper objectMapper;

    public ReservationPlaceServlet() {
        reservationPlaceMapper = new ReservationPlaceMapperImpl();
        validatorPostDTO = ReservationPlaceRequestDTOValidator.getInstance();
        validatorPutDTO = ReservationPlaceFullRequestDTOValidator.getInstance();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationPlaceService = (ReservationPlaceService) getServletContext().getAttribute(RESERVATION_PLACE_SERVICE);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    /**
     * @param req  запрос на авторизацию пользователя UserLogInAuthDto
     * @param resp ответ пользователю статус 200 и удаление сессии у пользователя
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        String path = ServletPathUtil.getPath(req);
        if(userRequestDTO != null) {
            if (path != null) {
                try {
                    int reservationPlaceId = Integer.parseInt(path);
                    ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
                    if(reservationPlace == null) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(objectMapper.writeValueAsString(
                                new ErrorResponse("Места с таким идентификатором не существует")));
                        return;
                    }
                    ReservationPlaceResponseDTO reservationPlaceResponseDTO = reservationPlaceMapper.toResponseDTO(reservationPlace);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(reservationPlaceResponseDTO));
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Идентификатор должен состоять из чисел")));
                }
            } else {
                List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();
                List<ReservationPlaceResponseDTO> reservationPlaceResponseDTOS =
                        reservationPlaceMapper.toResponseDTOList(reservationPlaceList);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(reservationPlaceResponseDTOS));
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
            if(adminRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else {
                ReservationPlaceRequestDTO reservationPlaceRequestDTO =
                        objectMapper.readValue(req.getReader(), ReservationPlaceRequestDTO.class);
                validatorPostDTO.validate(reservationPlaceRequestDTO);
                reservationPlaceService.saveReservationPlace(reservationPlaceRequestDTO);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new MessageResponse("Вы успешно создали место для бронирования")));
            }
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        } catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Тело запроса не соответствует требованиям")));
        } catch (FieldNotValidException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
            if(adminRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else {
                ReservationPlaceFullRequestDTO reservationPlaceFullRequestDTO =
                        objectMapper.readValue(req.getReader(), ReservationPlaceFullRequestDTO.class);
                validatorPutDTO.validate(reservationPlaceFullRequestDTO);
                ReservationPlace reservationPlace;
                if(reservationPlaceFullRequestDTO.getTypeId() == 1) {
                    reservationPlace = reservationPlaceMapper.toConferenceRoom(reservationPlaceFullRequestDTO);
                } else {
                    reservationPlace = reservationPlaceMapper.toWorkplace(reservationPlaceFullRequestDTO);
                }
                reservationPlaceService.updateReservationPlace(reservationPlace);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(reservationPlace));
            }
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        } catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Тело запроса не соответствует требованиям")));
        } catch (FieldNotValidException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
            String path = ServletPathUtil.getPath(req);
            if(adminRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else if(path != null){
                int reservationPlaceId = Integer.parseInt(path);
                if(reservationPlaceService.getReservationPlace(reservationPlaceId) == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Места с таким идентификатором не существует")));
                } else {
                    reservationPlaceService.deleteReservationPlace(reservationPlaceId);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new MessageResponse("Вы успешно удалили место для бронирования")));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new ErrorResponse("Не указан id места для бронирования")));
            }
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Идентификатор должен состоять из чисел")));
        }
    }
}
