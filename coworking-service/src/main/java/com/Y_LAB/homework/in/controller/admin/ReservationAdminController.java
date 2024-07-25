package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.exception.auth.NotEnoughRightsException;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.request.SortRequest;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /** Поле для валидации входящих токенов авторизации*/
    private final JwtUtil jwtUtil;

    /**
     * Получение брони по id
     * @param reservationId уникальный идентификатор брони
     * @param authHeader Заголовок с токеном авторизации
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
    public ResponseEntity<ReservationResponseDTO> getReservation(
            @PathVariable("id") Long reservationId,
            @RequestHeader("Authorization") String authHeader) throws ObjectNotFoundException, AuthorizeException, NotEnoughRightsException {
        jwtUtil.verifyAdminTokenFromHeader(authHeader);
        Reservation reservation = reservationService.getReservation(reservationId);
        ReservationResponseDTO reservationResponseDTO = reservationMapper.toResponseDTO(reservation);

        return ResponseEntity.ok(reservationResponseDTO);
    }

    /**
     * Получение всех броней
     * @param authHeader Заголовок с токеном авторизации
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
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(
            @RequestHeader("Authorization") String authHeader) throws AuthorizeException, NotEnoughRightsException {
        jwtUtil.verifyAdminTokenFromHeader(authHeader);
        List<Reservation> reservationList = reservationService.getAllReservations();
        List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);

        return ResponseEntity.ok(reservationResponseDTOS);
    }

    /**
     * Удаление брони по id
     * @param reservationId уникальный идентификатор брони
     * @param authHeader Заголовок с токеном авторизации
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
             @RequestHeader("Authorization") String authHeader) throws AuthorizeException, ObjectNotFoundException, NotEnoughRightsException {
        jwtUtil.verifyAdminTokenFromHeader(authHeader);
        reservationService.deleteReservation(reservationId);

        return ResponseEntity.ok(new MessageResponse("The reservation has been deleted"));
    }

    /**
     * Получение всех броней отсортированных по указанной сортировке
     * @param sortRequest Тело запроса содержащее тип сортировки
     * @param authHeader Заголовок с токеном авторизации
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
    public ResponseEntity<?> getReservationSortedByField(
            @RequestBody SortRequest sortRequest,
            @RequestHeader("Authorization") String authHeader) throws AuthorizeException, NotEnoughRightsException {
        jwtUtil.verifyAdminTokenFromHeader(authHeader);
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
    }
}
