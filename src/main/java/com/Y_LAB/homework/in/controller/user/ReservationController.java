package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationResponseDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.validation.ValidatorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
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

    @Autowired
    public ReservationController(ReservationMapper reservationMapper,
                                 @Qualifier("reservationReq") ValidatorDTO<ReservationRequestDTO> validatorPostDTO,
                                 ReservationService reservationService,
                                 ReservationPlaceService reservationPlaceService,
                                 UserService userService) {
        this.reservationMapper = reservationMapper;
        this.validatorPostDTO = validatorPostDTO;
        this.reservationService = reservationService;
        this.reservationPlaceService = reservationPlaceService;
        this.userService = userService;
    }

    /**
     * Получение брони пользователя по id
     * @param reservationId уникальный идентификатор брони
     * @param req Http запрос для получения сессии пользователя
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
            HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            Reservation reservation = reservationService.getReservation(reservationId);
            if(reservation != null && reservation.getUserId() == userId) {
                ReservationResponseDTO reservationResponseDTO = reservationMapper.toResponseDTO(reservation);
                return ResponseEntity.ok(reservationResponseDTO);
            } else {
                throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
            }
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех броней пользователя
     * @param req Http запрос для получения сессии пользователя
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
            (HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            List<Reservation> reservationList = reservationService.getAllUserReservations(userId);
            List<ReservationResponseDTO> reservationResponseDTOS = reservationMapper.toResponseDTOList(reservationList);
            return ResponseEntity.ok(reservationResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Добавление новой пользовательской брони
     * @param req Http запрос для получения сессии пользователя
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
             HttpServletRequest req) throws AuthorizeException, FieldNotValidException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            validatorPostDTO.validate(reservationRequestDTO);
            reservationService.saveReservation(reservationRequestDTO, userId);
            return ResponseEntity.ok(new MessageResponse("The reservation has been created"));
        }
    }

    /**
     * Обновление пользовательской брони
     * @param req Http запрос для получения сессии пользователя
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
             HttpServletRequest req) throws AuthorizeException, FieldNotValidException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
            validatorPostDTO.validate(reservationRequestDTO);
            Reservation reservation = reservationService.getReservation(reservationId);
            if(reservation != null && reservation.getUserId() == userId) {
                ReservationPlace reservationPlace =
                        reservationPlaceService.getReservationPlace(reservationRequestDTO.getReservationPlaceId());
                reservation = reservationMapper.toReservation(reservationRequestDTO, reservationId, userId, reservationPlace);
                reservationService.updateReservation(reservation);
                return ResponseEntity.ok(new MessageResponse("The reservation has been updated"));
            } else {
                throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
            }
        }
    }

    /**
     * Удаление брони пользователя по id брони
     * @param reservationId уникальный идентификатор брони
     * @param req Http запрос для получения сессии пользователя
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
             HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        long userId = userService.getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword()).getId();
        Reservation reservation = reservationService.getReservation(reservationId);
        if(reservation != null && reservation.getUserId() == userId) {
            reservationService.deleteReservation(reservationId);
            return ResponseEntity.ok(new MessageResponse("The reservation has been deleted"));
        } else {
            throw new ObjectNotFoundException("Reservation with id " + reservationId + " does not exists");
        }
    }
}
