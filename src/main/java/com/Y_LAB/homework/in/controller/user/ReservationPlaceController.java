package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.request.LocalDateRequest;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.*;

/**
 * REST контроллер для взаимодействия с местами для бронирования
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_RESERVATION_PLACE_PATH)
@Tag(name = "Места для бронирования")
public class ReservationPlaceController {

    /** Поле для преобразования DTO объектов мест для бронирования*/
    private final ReservationPlaceMapper reservationPlaceMapper;

    /** Поле сервиса мест для бронирования*/
    private final ReservationPlaceService reservationPlaceService;

    /** Поле сервиса свободных временных ячеек для брони*/
    private final FreeReservationSlotService freeReservationSlotService;

    /**
     * Получение места для бронирования по id
     * @param reservationPlaceId уникальный идентификатор места для бронирования
     * @param req Http запрос для получения сессии пользователя
     * @return DTO Response Объект места для бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если места для бронирования с таким id не существует
     */
    @Operation(summary = "Получение места для бронирования по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Место найдено"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "Место для бронирования с указанным id не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationPlaceResponseDTO> getReservationPlace(
            @PathVariable("id") Integer reservationPlaceId,
            HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace == null) {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
            ReservationPlaceResponseDTO reservationPlaceResponseDTO = reservationPlaceMapper.toResponseDTO(reservationPlace);
            return ResponseEntity.ok(reservationPlaceResponseDTO);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех мест для бронирования
     * @param req Http запрос для получения сессии пользователя
     * @return Список содержащий DTO Response Объекты всех мест для бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     */
    @Operation(summary = "Получение всех мест для бронирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех мест для бронирования"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping
    public ResponseEntity<List<ReservationPlaceResponseDTO>> getAllReservationPlaces(HttpServletRequest req)
            throws AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();
            List<ReservationPlaceResponseDTO> reservationPlaceResponseDTOS =
                    reservationPlaceMapper.toResponseDTOList(reservationPlaceList);
            return ResponseEntity.ok(reservationPlaceResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех доступных дат для бронирования места по id
     * @param reservationPlaceId Уникальный идентификатор места для бронирования
     * @param req Http запрос для получения сессии пользователя
     * @return Объект содержащий место и доступные даты для его бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если места для бронирования с таким id не существует
     */
    @Operation(summary = "Получение всех доступных дат для бронирования места по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех доступных дат для бронирования места"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "Место для бронирования с указанным id не найдено")
    })
    @GetMapping(CONTROLLER_DATE_SLOT_PATH + "/{id}")
    public ResponseEntity<ReservationPlaceDateSlotResponseDTO> getAvailableDatesForPlace
            (@PathVariable("id") Integer reservationPlaceId,
             HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace == null) {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
            List<LocalDate> availableDatesForReservePlace =
                    freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
            ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                    reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
            return ResponseEntity.ok(reservationPlaceDateSlotResponseDTO);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех доступных дат для бронирования всех мест
     * @param req Http запрос для получения сессии пользователя
     * @return Список объектов содержащих места и доступные даты для их бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     */
    @Operation(summary = "Получение всех доступных дат для бронирования всех мест")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех доступных дат для бронирования всех мест"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping(CONTROLLER_DATE_SLOT_PATH)
    public ResponseEntity<List<ReservationPlaceDateSlotResponseDTO>> getAllAvailableDatesForPlaces
            (HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO != null) {
            List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();
            List<ReservationPlaceDateSlotResponseDTO> freeSlotResponseDTOS = new ArrayList<>();
            for(ReservationPlace reservationPlace : reservationPlaceList) {
                List<LocalDate> availableDatesForReservePlace =
                        freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
                ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                        reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
                freeSlotResponseDTOS.add(reservationPlaceDateSlotResponseDTO);
            }
            return ResponseEntity.ok(freeSlotResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех доступных мест для бронирования на указанную дату
     * @param localDateRequest Объект содержащий желаемую дату для бронирования мест
     * @param req Http запрос для получения сессии пользователя
     * @return Список объектов мест для бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если нет мест для бронирования на указанную дату
     */
    @Operation(summary = "Получение всех доступных мест для бронирования на указанную дату")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех доступных мест для бронирования на указанную дату"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description = "За указанную дату нет доступных мест для бронирования")
    })
    @PostMapping(CONTROLLER_DATE_SLOT_PATH)
    public ResponseEntity<List<ReservationPlace>> getAvailablePlaceForReserveByDate
            (@RequestBody LocalDateRequest localDateRequest,
             HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            List<ReservationPlace> availableReservationPlaces =
                    freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate());
            if(availableReservationPlaces.isEmpty()) {
                throw new ObjectNotFoundException(
                        "Place for reservation on date " + localDateRequest.getLocalDate() + " does not exists");
            } else {
                return ResponseEntity.ok(availableReservationPlaces);
            }
        }
    }

    /**
     * Получение всех доступных временных промежутков в указанную дату для бронирования места по id
     * @param reservationPlaceId Уникальный идентификатор места для бронирования
     * @param localDateRequest Объект содержащий желаемую дату для бронирования мест
     * @param req Http запрос для получения сессии пользователя
     * @return Список доступных промежутков часов для бронирования места в формате (09-10, 10-11)
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ObjectNotFoundException Если места для бронирования с указанным id не существует
     */
    @Operation(summary = "Получение всех доступных временных промежутков в указанную дату для бронирования места по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Возращение всех доступных временных промежутков для бронирования мест на указанную дату"),
            @ApiResponse(responseCode = "400", description = "Тело запроса не валидно"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "404", description =
                    "За указанную дату нет доступных промежутков времени для бронирования указанного места по id")
    })
    @PostMapping(CONTROLLER_TIME_SLOT_PATH + "/{id}")
    public ResponseEntity<List<String>> getAvailableTimesForReservation
            (@PathVariable("id") Integer reservationPlaceId,
             @RequestBody LocalDateRequest localDateRequest,
             HttpServletRequest req) throws AuthorizeException, ObjectNotFoundException {
        HttpSession session = req.getSession();
        UserRequestDTO userRequestDTO = (UserRequestDTO) session.getAttribute(SESSION_USER);
        if(userRequestDTO == null) {
            throw new AuthorizeException("You are not logged in");
        } else {
            ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
            if(reservationPlace != null) {
                return ResponseEntity.ok(freeReservationSlotService
                        .getAllAvailableTimeListForReservation(reservationPlace, localDateRequest.getLocalDate()));
            } else {
                throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
            }
        }
    }
}
