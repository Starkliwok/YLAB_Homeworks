package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.ReservationPlaceRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.request.SortPlaceTypeRequest;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import com.Y_LAB.homework.model.response.ErrorResponse;
import com.Y_LAB.homework.model.response.MessageResponse;
import com.Y_LAB.homework.service.ReservationPlaceService;
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

import static com.Y_LAB.homework.constants.ReservationPlaceConstants.RESERVATION_PLACE_CONFERENCE_ROOM_TYPE;
import static com.Y_LAB.homework.constants.ReservationPlaceConstants.RESERVATION_PLACE_WORKPLACE_TYPE;
import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * REST контроллер панели администратора для получения отсортированных мест для бронирований
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_RESERVATION_PLACE_PATH)
@Tag(name = "(ADMIN) Места для бронирования")
public class ReservationPlaceAdminController {

    /** Поле для преобразования DTO объектов мест для бронирования*/
    private final ReservationPlaceMapper reservationPlaceMapper;

    /** Поле для валидации DTO объектов мест для бронирования*/
    private final ValidatorDTO<ReservationPlaceRequestDTO> validatorPostDTO;

    /** Поле сервиса мест для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    @Autowired
    public ReservationPlaceAdminController(ReservationPlaceMapper reservationPlaceMapper,
                                           @Qualifier("placeReq") ValidatorDTO<ReservationPlaceRequestDTO> validatorPostDTO,
                                           ReservationPlaceService reservationPlaceService) {
        this.reservationPlaceMapper = reservationPlaceMapper;
        this.validatorPostDTO = validatorPostDTO;
        this.reservationPlaceService = reservationPlaceService;
    }

    /**
     * Получение всех мест для бронирования отсортированных по указанной сортировке
     * @param sortRequest Тело запроса содержащее тип сортировки и тип помещения
     * @param req Http запрос для получения сессии пользователя
     * @return Отсортированные места для бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение отсортированных мест для бронирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение всех мест отсортированные по указанной сортировке " +
                    "(type) и указанному типу места (conference room/workplace)"),
            @ApiResponse(responseCode = "400", description = "Указан недоступный тип сортировки/указан недоступный тип места"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором")
    })
    @PostMapping(CONTROLLER_SORT_PATH)
    public ResponseEntity<?> getReservationPlacesSortedByType(@RequestBody SortPlaceTypeRequest sortRequest,
                                                              HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            if (sortRequest.getSortType().equals("type")) {
                if (sortRequest.getPlaceType().equalsIgnoreCase(RESERVATION_PLACE_CONFERENCE_ROOM_TYPE)) {
                    List<ReservationPlace> reservationList =
                            reservationPlaceService.getAllReservationPlacesByTypes(new ConferenceRoom());
                    List<ReservationPlaceResponseDTO> reservationResponseDTOS =
                            reservationPlaceMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                } else if (sortRequest.getPlaceType().equalsIgnoreCase(RESERVATION_PLACE_WORKPLACE_TYPE)) {
                    List<ReservationPlace>
                            reservationList = reservationPlaceService.getAllReservationPlacesByTypes(new Workplace());
                    List<ReservationPlaceResponseDTO>
                            reservationResponseDTOS = reservationPlaceMapper.toResponseDTOList(reservationList);
                    return ResponseEntity.ok(reservationResponseDTOS);
                } else {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Enter place type"));
                }
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Enter sort type"));
            }
        }
    }

    /**
     * Добавление нового места для бронирования
     * @param reservationPlaceRequestDTO Тело запроса DTO объекта места для бронирования
     * @param req Http запрос для получения сессии пользователя
     * @return Сообщение об успешном сохранении брони
     * @throws FieldNotValidException Если тело запроса не валидно
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Добавление нового места для бронирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Место успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Тело запроса не валидно"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором")
    })
    @PostMapping
    public ResponseEntity<MessageResponse> saveReservationPlace
            (@RequestBody ReservationPlaceRequestDTO reservationPlaceRequestDTO,
             HttpServletRequest req) throws AuthorizeException, FieldNotValidException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            validatorPostDTO.validate(reservationPlaceRequestDTO);
            reservationPlaceService.saveReservationPlace(reservationPlaceRequestDTO);
            return ResponseEntity.ok(new MessageResponse("The place has been created"));
        }
    }

    /**
     * Обновление места для бронирования
     * @param reservationPlaceId Уникальный идентификатор места для бронирования
     * @param reservationPlaceRequestDTO Тело запроса DTO объекта места для бронирования
     * @param req Http запрос для получения сессии пользователя
     * @return Обновлённый объект места для бронирования
     * @throws FieldNotValidException Если тело запроса не валидно
     * @throws ObjectNotFoundException Если места с таким id не существует
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Обновление места для бронирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Место успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Тело запроса не валидно"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Место с указанным id не найдено")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationPlace> updateReservationPlace
            (@PathVariable("id") Integer reservationPlaceId,
             @RequestBody ReservationPlaceRequestDTO reservationPlaceRequestDTO,
             HttpServletRequest req) throws FieldNotValidException, AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            validatorPostDTO.validate(reservationPlaceRequestDTO);
            ReservationPlace reservationPlace =
                    reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace == null) {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
            if(reservationPlaceRequestDTO.getTypeId() == 1) {
               reservationPlace = reservationPlaceMapper.toConferenceRoom(reservationPlaceRequestDTO);
            } else if (reservationPlaceRequestDTO.getTypeId() == 2){
                reservationPlace = reservationPlaceMapper.toWorkplace(reservationPlaceRequestDTO);
            }
            reservationPlaceService.updateReservationPlace(reservationPlace);
            return ResponseEntity.ok(reservationPlace);
        }
    }

    /**
     * Удаление места для бронирования по id
     * @param reservationPlaceId Уникальный идентификатор места для бронирования
     * @param req Http запрос для получения сессии пользователя
     * @return Сообщение об успешном обновлении брони
     * @throws ObjectNotFoundException Если места с таким id не существует
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Удаление места для бронирования по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Место успешно удалено"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Место с таким id не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReservationPlace
            (@PathVariable("id") Integer reservationPlaceId,
             HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if(adminRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        }
        if(reservationPlaceService.getReservationPlace(reservationPlaceId) == null) {
            throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
        } else {
            reservationPlaceService.deleteReservationPlace(reservationPlaceId);
            return ResponseEntity.ok(new MessageResponse("Place with id " + reservationPlaceId + " has been deleted"));
        }
    }
}
