package com.Y_LAB.homework.in.servlet.admin;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.mapper.ReservationMapperImpl;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.service.ReservationService;
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
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;

@WebServlet(urlPatterns = {CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH
                , CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH + "/*"})
public class ReservationAdminServlet extends HttpServlet {

    private final ReservationMapper reservationMapper;

    private ObjectMapper objectMapper;

    private ReservationService reservationService;

    public ReservationAdminServlet() {
        reservationMapper = new ReservationMapperImpl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationService = (ReservationService) getServletContext().getAttribute(RESERVATION_SERVICE);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        String path = ServletPathUtil.getPath(req);
        try {
            AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
            if (adminRequestDTO != null) {
                if (path != null) {
                    int reservationId = Integer.parseInt(path);
                    Reservation reservation = reservationService.getReservation(reservationId);
                    if (reservation != null) {
                        ReservationResponseDTO reservationResponseDTO = reservationMapper.toResponseDTO(reservation);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTO));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(objectMapper.writeValueAsString(
                                new ErrorResponse("Брони с таким идентификатором не существует")));
                    }
                } else {
                    List<Reservation> reservationList = reservationService.getAllReservations();
                    List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTOS));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
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
            } else if(path != null) {
                int reservationId = Integer.parseInt(path);
                Reservation reservation = reservationService.getReservation(reservationId);
                if(reservation != null) {
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
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        }
    }
}
