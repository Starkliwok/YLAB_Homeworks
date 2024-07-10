package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.mapper.ReservationMapperImpl;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.request.SortRequest;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.service.ReservationService;
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

import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.controller.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * Сервлет панели администратора для получения отсортированных броней пользователей
 * @author Денис Попов
 * @version 1.0
 */
@WebServlet(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + CONTROLLER_SORT_PATH)
public class ReservationSortAdminServlet extends HttpServlet {

    private final ReservationMapper reservationMapper;
    private ObjectMapper objectMapper;
    private ReservationService reservationService;

    public ReservationSortAdminServlet() {
        reservationMapper = new ReservationMapperImpl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationService = (ReservationService) getServletContext().getAttribute(RESERVATION_SERVICE);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        try {
            AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
            if (adminRequestDTO != null) {
                SortRequest sortRequest = objectMapper.readValue(req.getReader(), SortRequest.class);
                switch (sortRequest.getSortType()) {
                    case "user" -> {
                        List<Reservation> reservationList = reservationService.getAllReservationsByUsers();
                        List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTOS));
                    }
                    case "date" -> {
                        List<Reservation> reservationList = reservationService.getAllReservationsByDate();
                        List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTOS));
                    }
                    default -> {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Введите тип сортировки")));
                    }
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            }
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        }  catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Тело запроса не соответствует требованиям")));
        }
    }
}
