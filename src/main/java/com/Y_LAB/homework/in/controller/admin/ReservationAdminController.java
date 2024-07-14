package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.request.SortRequest;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * REST контроллер панели администратора для управления бронями
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PATH)
@Tag(name = "(ADMIN) Брони пользователей")
public class ReservationAdminController {

    /** Поле для преобразования DTO объектов броней*/
    private final ReservationMapper reservationMapper;

    /** Поле сервиса броней*/
    private final ReservationService reservationService;

    /**
     * Получение брони по id
     * @param reservationId уникальный идентификатор брони
     * @param req Http запрос для получения сессии пользователя
     * @return Бронь
     * @throws ObjectNotFoundException Если брони с таким id не существует
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение брони по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронь найдена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Бронь с указанным id не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservation(@PathVariable("id") Long reservationId,
                                            HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            Reservation reservation = reservationService.getReservation(reservationId);
            ReservationResponseDTO reservationResponseDTO = reservationMapper.toResponseDTO(reservation);
            return ResponseEntity.ok(reservationResponseDTO);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех броней
     * @param req Http запрос для получения сессии пользователя
     * @return Список всех броней
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение всех броней пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех броней"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором")
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            List<Reservation> reservationList = reservationService.getAllReservations();
            List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
            return ResponseEntity.ok(reservationResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Удаление брони по id
     * @param reservationId уникальный идентификатор брони
     * @param req Http запрос для получения сессии пользователя
     * @return Сообщение об успешном удалении брони
     * @throws ObjectNotFoundException Если брони с таким id не существует
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Удаление брони по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронь успешно удалена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Бронь с указанным id не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReservation
            (@PathVariable("id") Long reservationId,
             HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.ok(new MessageResponse("The reservation has been deleted"));
    }

    /**
     * Получение всех броней отсортированных по указанной сортировке
     * @param sortRequest Тело запроса содержащее тип сортировки
     * @param req Http запрос для получения сессии пользователя
     * @return Отсортированные брони
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение отсортированных броней")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение всех броней отсортированные по указанной сортировке user/date"),
            @ApiResponse(responseCode = "400", description = "Указан недоступный тип сортировки"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором")
    })
    @PostMapping(CONTROLLER_SORT_PATH)
    public ResponseEntity<?> getReservationSortedByField(@RequestBody SortRequest sortRequest,
                                                         HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            switch (sortRequest.getSortType()) {
                case "user" -> {
                    List<Reservation> reservationList = reservationService.getAllReservationsByUsers();
                    List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                }
                case "date" -> {
                    List<Reservation> reservationList = reservationService.getAllReservationsByDate();
                    List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                }
                default -> {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Enter sort type"));
                }
            }
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }
}
