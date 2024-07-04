package com.Y_LAB.homework.in.servlet;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.mapper.ReservationMapperImpl;
import com.Y_LAB.homework.model.dto.request.*;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.validation.ValidatorDTO;
import com.Y_LAB.homework.validation.impl.ReservationPutRequestDTOValidator;
import com.Y_LAB.homework.validation.impl.ReservationRequestDTOValidator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;

@WebServlet(urlPatterns = {CONTROLLER_RESERVATION_PATH, CONTROLLER_RESERVATION_PATH + "/*"})
public class ReservationUserServlet extends HttpServlet {
    private final ObjectMapper objectMapper;

    private final ReservationMapper reservationMapper;

    private final ValidatorDTO<ReservationRequestDTO> validatorPostDTO;

    private final ValidatorDTO<ReservationPutRequestDTO> validatorPutDTO;

    private ReservationService reservationService;

    private UserService userService;

    public ReservationUserServlet() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new JavaTimeModule());
        reservationMapper = new ReservationMapperImpl();
        validatorPostDTO = ReservationRequestDTOValidator.getInstance();
        validatorPutDTO = ReservationPutRequestDTOValidator.getInstance();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationService = (ReservationService) getServletContext().getAttribute(RESERVATION_SERVICE);
        userService = (UserService) getServletContext().getAttribute(USER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        String path = ServletPathUtil.getPath(req);
        if(userRequestDTO != null) {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            if (path != null) {
                try {
                    int reservationId = Integer.parseInt(path);
                    Reservation reservation = reservationService.getReservation(reservationId);
                    if(reservation != null && reservation.getUserId() == userId) {
                        ReservationResponseDTO reservationResponseDTO = reservationMapper.toResponseDTO(reservation);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTO));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(objectMapper.writeValueAsString(
                                new ErrorResponse("Брони с таким идентификатором не существует")));
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Идентификатор должен состоять из чисел")));
                }
            } else {
                List<Reservation> reservationList = reservationService.getAllUserReservations(userId);
                List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTOS));
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
        }
    }

    /**
     * @param req  запрос на авторизацию пользователя UserLogInAuthDto
     * @param resp ответ пользователю статус 200 и удаление сессии у пользователя
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
            if(userRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else {
                long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
                ReservationRequestDTO reservationRequestDTO =
                        objectMapper.readValue(req.getReader(), ReservationRequestDTO.class);
                validatorPostDTO.validate(reservationRequestDTO);
                reservationService.saveReservation(reservationRequestDTO, userId);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
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
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
            if(userRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else {
                long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
                ReservationPutRequestDTO reservationPutRequestDTO =
                        objectMapper.readValue(req.getReader(), ReservationPutRequestDTO.class);
                validatorPutDTO.validate(reservationPutRequestDTO);
                Reservation reservation = reservationService.getReservation(reservationPutRequestDTO.getId());
                if(reservation.getUserId() == userId) {
                    ReservationPlaceService reservationPlaceService = new ReservationPlaceServiceImpl();
                    ReservationPlace reservationPlace =
                            reservationPlaceService.getReservationPlace(reservationPutRequestDTO.getReservationPlaceId());
                    reservation = Reservation.builder()
                            .id(reservationPutRequestDTO.getId())
                            .userId(userId)
                            .startDate(reservationPutRequestDTO.getStartDate())
                            .endDate(reservationPutRequestDTO.getEndDate())
                            .reservationPlace(reservationPlace).build();
                    reservationService.updateReservation(reservation);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Брони с таким идентификатором не существует")));
                }
            }
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
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
            String path = ServletPathUtil.getPath(req);
            if(userRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else if(path != null) {
                int reservationId = Integer.parseInt(path);
                long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
                Reservation reservation = reservationService.getReservation(reservationId);
                if(reservation != null && reservation.getUserId() == userId) {
                    reservationService.deleteReservation(reservationId);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Брони с таким идентификатором не существует")));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new ErrorResponse("Укажите идентификатор брони")));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Идентификатор должен состоять из чисел")));
        }
    }
}