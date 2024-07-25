package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_RESERVATION_PATH;

/**
 * REST контроллер для взаимодействия с бронированиями пользователей
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequestMapping(CONTROLLER_RESERVATION_PATH)
@Tag(name = "Брони пользователей")
public class ReservationController {

    /** Поле для преобразования DTO объектов броней*/
    private final ReservationMapper reservationMapper;

    /** Поле для валидации DTO объектов брони*/
    private final ValidatorDTO<ReservationRequestDTO> validatorPostDTO;

    /** Поле сервиса броней*/
    private final ReservationService reservationService;

    /** Поле сервиса мест для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса пользователей*/
    private final UserService userService;

    /** Поле для валидации входящих токенов авторизации*/
    private final JwtUtil jwtUtil;

    @Autowired
    public ReservationController(ReservationMapper reservationMapper,
                                 @Qualifier("reservationReq") ValidatorDTO<ReservationRequestDTO> validatorPostDTO,
                                 ReservationService reservationService,
                                 ReservationPlaceService reservationPlaceService,
                                 UserService userService,
                                 JwtUtil jwtUtil) {
        this.reservationMapper = reservationMapper;
        this.validatorPostDTO = validatorPostDTO;
        this.reservationService = reservationService;
        this.reservationPlaceService = reservationPlaceService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Получение брони пользователя по id
     * @param authHeader Заголовок с токеном авторизации
     * @param reservationId уникальный идентификатор брони
     * @return Бронь пользователя
     * @throws ObjectNotFoundException Если брони с таким id не существует или она не принадлежит пользователю
     * @throws AuthorizeException Если пользователь не авторизован
     */
    @Operation(summary = "Получение брони пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронь пользователя найдена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "Бронь пользователя с указанным id не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getUserReservation(
            @PathVariable("id") Long reservationId,
            @RequestHeader("Authorization") String authHeader) throws AuthorizeException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        long userId = userService.getUserId(jwtUtil.getUserLogin(authHeader));
        Reservation reservation = reservationService.getUserReservation(reservationId, userId);

        return ResponseEntity.ok(reservationMapper.toResponseDTO(reservation));
    }

    /**
     * Получение всех броней пользователя
     * @param authHeader Заголовок с токеном авторизации
     * @return Список всех броней пользователя
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если пользователь не имеет броней
     */
    @Operation(summary = "Получение всех броней пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех броней пользователя"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "У пользователя нет броней")
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllUserReservations
            (@RequestHeader("Authorization") String authHeader) throws AuthorizeException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        long userId = userService.getUserId(jwtUtil.getUserLogin(authHeader));
        List<Reservation> reservationList = reservationService.getAllUserReservations(userId);

        return ResponseEntity.ok(reservationMapper.toResponseDTOList(reservationList));
    }

    /**
     * Добавление новой пользовательской брони
     * @param authHeader Заголовок с токеном авторизации
     * @param reservationRequestDTO DTO объект содержащий данные для создания брони
     * @return Сообщение об успешном создании брони
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws FieldNotValidException Если тело запроса не валидно
     * @throws ObjectNotFoundException Если бронь содержит помещение которого не существует
     */
    @Operation(summary = "Добавление новой брони")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронь успешно сохранена"),
            @ApiResponse(responseCode = "400", description = "Тело запроса не валидно"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "Указанное место для бронирования не существует")
    })
    @PostMapping
    public ResponseEntity<MessageResponse> saveUserReservation
            (@RequestBody ReservationRequestDTO reservationRequestDTO,
             @RequestHeader("Authorization") String authHeader) throws AuthorizeException, FieldNotValidException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        long userId = userService.getUserId(jwtUtil.getUserLogin(authHeader));
        validatorPostDTO.validate(reservationRequestDTO);
        reservationService.saveReservation(reservationRequestDTO, userId);

        return ResponseEntity.ok(new MessageResponse("The reservation has been created"));
    }

    /**
     * Обновление пользовательской брони
     * @param authHeader Заголовок с токеном авторизации
     * @param reservationId уникальный идентификатор брони
     * @param reservationRequestDTO DTO объект содержащий данные для создания брони
     * @return Сообщение об успешном обновлении брони
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws FieldNotValidException Если тело запроса не валидно
     * @throws ObjectNotFoundException Если бронь содержит помещение которого не существует или брони с таким id не существует
     */
    @Operation(summary = "Обновление брони")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронь успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Тело запроса не валидно"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "Указанное место для бронирования или бронь не существует")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUserReservation
            (@PathVariable("id") Long reservationId,
             @RequestBody ReservationRequestDTO reservationRequestDTO,
             @RequestHeader("Authorization") String authHeader) throws AuthorizeException, FieldNotValidException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        long userId = userService.getUserId(jwtUtil.getUserLogin(authHeader));
        validatorPostDTO.validate(reservationRequestDTO);
        reservationService.getUserReservation(reservationId, userId);
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationRequestDTO.getReservationPlaceId());
        Reservation reservation = reservationMapper.toReservation(reservationRequestDTO, reservationId, userId, reservationPlace);
        reservationService.updateReservation(reservation);

        return ResponseEntity.ok(new MessageResponse("The reservation has been updated"));
    }

    /**
     * Удаление брони пользователя по id брони
     * @param reservationId уникальный идентификатор брони
     * @param authHeader Заголовок с токеном авторизации
     * @return Сообщение об успешном удалении брони
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если брони с таким id не существует
     */
    @Operation(summary = "Удаление брони пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронь пользователя успешно удалена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "Бронь пользователя с указанным id не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUserReservation
            (@PathVariable("id") Long reservationId,
             @RequestHeader("Authorization") String authHeader) throws AuthorizeException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        long userId = userService.getUserId(jwtUtil.getUserLogin(authHeader));
        reservationService.deleteUserReservation(reservationId, userId);

        return ResponseEntity.ok(new MessageResponse("The reservation has been deleted"));
    }
}
