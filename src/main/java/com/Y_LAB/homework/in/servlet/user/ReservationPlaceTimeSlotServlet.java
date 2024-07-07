package com.Y_LAB.homework.in.servlet.user;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.request.IdWithLocalDateRequest;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.validation.NumberValidator;
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

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_TIME_SLOT_PATH;
import static com.Y_LAB.homework.validation.constants.NameOfFieldsForValidationConstants.FIELD_ID;

@WebServlet(CONTROLLER_RESERVATION_PATH + CONTROLLER_TIME_SLOT_PATH)
public class ReservationPlaceTimeSlotServlet extends HttpServlet {

    private final NumberValidator numberValidator;

    private ReservationPlaceService reservationPlaceService;

    private ObjectMapper objectMapper;

    private FreeReservationSlotService freeReservationSlotService;

    public ReservationPlaceTimeSlotServlet() {
        numberValidator = NumberValidator.getInstance();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationPlaceService = (ReservationPlaceService) getServletContext().getAttribute(RESERVATION_PLACE_SERVICE);
        freeReservationSlotService = (FreeReservationSlotService) getServletContext().getAttribute(FREE_RESERVATION_SLOT_SERVICE);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
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
                IdWithLocalDateRequest idWithLocalDateRequest =
                        objectMapper.readValue(req.getReader(), IdWithLocalDateRequest.class);
                numberValidator.validate(idWithLocalDateRequest.getId(), FIELD_ID);
                ReservationPlace reservationPlace = reservationPlaceService
                        .getReservationPlace(idWithLocalDateRequest.getId());
                if(reservationPlace != null) {
                    resp.getWriter().write(objectMapper.writeValueAsString(freeReservationSlotService
                            .getAllAvailableTimeListForReservation(reservationPlace
                                    , idWithLocalDateRequest.getLocalDate())));
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Места с таким идентификатором не существует")));
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
}
