package com.Y_LAB.homework.in.servlet.admin;

import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.mapper.ReservationPlaceMapperImpl;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.request.SortPlaceTypeRequest;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.model.response.ErrorResponse;
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
import java.util.List;

import static com.Y_LAB.homework.constants.ReservationPlaceConstants.RESERVATION_PLACE_CONFERENCE_ROOM_TYPE;
import static com.Y_LAB.homework.constants.ReservationPlaceConstants.RESERVATION_PLACE_WORKPLACE_TYPE;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.*;

/**
 * Сервлет панели администратора для получения отсортированных мест для бронирований
 * @author Денис Попов
 * @version 1.0
 */
@WebServlet(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH + CONTROLLER_SORT_PATH)
public class ReservationPlaceSortAdminServlet extends HttpServlet {

    private final ReservationPlaceMapper reservationPlaceMapper;

    private ObjectMapper objectMapper;

    private ReservationPlaceService reservationPlaceService;

    public ReservationPlaceSortAdminServlet() {
        reservationPlaceMapper = new ReservationPlaceMapperImpl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservationPlaceService = (ReservationPlaceService) getServletContext().getAttribute(RESERVATION_PLACE_SERVICE);
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
                SortPlaceTypeRequest sortRequest = objectMapper.readValue(req.getReader(), SortPlaceTypeRequest.class);
                if (sortRequest.getSortType() != null && sortRequest.getSortType().equals("type")) {
                    if (sortRequest.getPlaceType() != null && sortRequest.getPlaceType().equalsIgnoreCase(RESERVATION_PLACE_CONFERENCE_ROOM_TYPE)) {
                        List<ReservationPlace> reservationList =
                                reservationPlaceService.getAllReservationPlacesByTypes(new ConferenceRoom());
                        List<ReservationPlaceResponseDTO> reservationResponseDTOS =
                                reservationPlaceMapper.toResponseDTOList(reservationList);

                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTOS));
                    } else if (sortRequest.getPlaceType() != null && sortRequest.getPlaceType().equalsIgnoreCase(RESERVATION_PLACE_WORKPLACE_TYPE)) {
                        List<ReservationPlace> reservationList =
                                reservationPlaceService.getAllReservationPlacesByTypes(new Workplace());
                        List<ReservationPlaceResponseDTO> reservationResponseDTOS =
                                reservationPlaceMapper.toResponseDTOList(reservationList);

                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(reservationResponseDTOS));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Введите тип помещения")));
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Введите тип сортировки")));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            }
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        } catch (JsonMappingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Тело запроса не соответствует требованиям")));
        }
    }
}
