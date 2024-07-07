package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.mapper.ReservationPlaceMapperImpl;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.request.LocalDateRequest;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_DATE_SLOT_PATH;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;

/**
 * Сервлет для отображения свободных дат для бронирования помещений
 * @author Денис Попов
 * @version 1.0
 */
@WebServlet(urlPatterns = {CONTROLLER_RESERVATION_PATH + CONTROLLER_DATE_SLOT_PATH
        , CONTROLLER_RESERVATION_PATH + CONTROLLER_DATE_SLOT_PATH + "/*"})
public class ReservationPlaceDateSlotServlet extends HttpServlet {
    private ObjectMapper objectMapper;

    private final ReservationPlaceMapper reservationPlaceMapper;

    private ReservationPlaceService reservationPlaceService;

    private FreeReservationSlotService freeReservationSlotService;

    public ReservationPlaceDateSlotServlet() {
        reservationPlaceMapper = new ReservationPlaceMapperImpl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationPlaceService = (ReservationPlaceService) getServletContext().getAttribute(RESERVATION_PLACE_SERVICE);
        freeReservationSlotService = (FreeReservationSlotService) getServletContext().getAttribute(FREE_RESERVATION_SLOT_SERVICE);
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
                    List<LocalDate> availableDatesForReservePlace =
                            freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
                    ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                            reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(reservationPlaceDateSlotResponseDTO));
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Идентификатор должен состоять из чисел")));
                }
            } else {
                List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();
                List<ReservationPlaceDateSlotResponseDTO> freeSlotResponseDTOS = new ArrayList<>();
                for(ReservationPlace reservationPlace : reservationPlaceList) {
                    List<LocalDate> availableDatesForReservePlace =
                            freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
                    ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                            reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
                    freeSlotResponseDTOS.add(reservationPlaceDateSlotResponseDTO);
                }
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(freeSlotResponseDTOS));
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
            UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
            if(userRequestDTO == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            } else {
                LocalDateRequest localDateRequest = objectMapper.readValue(req.getReader(), LocalDateRequest.class);
                List<ReservationPlace> availableReservationPlaces =
                        freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate());
                if(availableReservationPlaces.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(objectMapper.writeValueAsString(new MessageResponse("Нет доступных мест для бронирования за указанную дату")));
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(availableReservationPlaces));
                }
            }
        } catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Тело запроса не соответствует требованиям")));
        }
    }
}
