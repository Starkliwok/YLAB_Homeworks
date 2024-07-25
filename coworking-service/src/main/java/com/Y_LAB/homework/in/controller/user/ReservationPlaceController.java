package com.Y_LAB.homework.in.controller.user;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.ReservationPlaceMapper;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceDateSlotResponseDTO;
import com.Y_LAB.homework.model.dto.response.ReservationPlaceResponseDTO;
import com.Y_LAB.homework.model.request.LocalDateRequest;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.security.JwtUtil;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.ReservationPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    /** Поле для валидации входящих токенов авторизации*/
    private final JwtUtil jwtUtil;

    /**
     * Получение места для бронирования по id
     * @param reservationPlaceId уникальный идентификатор места для бронирования
     * @param authHeader Заголовок с токеном авторизации
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
            @RequestHeader("Authorization") String authHeader) throws ObjectNotFoundException, AuthorizeException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
        if(reservationPlace == null) {
            throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
        }
        ReservationPlaceResponseDTO reservationPlaceResponseDTO = reservationPlaceMapper.toResponseDTO(reservationPlace);
        return ResponseEntity.ok(reservationPlaceResponseDTO);
    }

    /**
     * Получение всех мест для бронирования
     * @param authHeader Заголовок с токеном авторизации
     * @return Список содержащий DTO Response Объекты всех мест для бронирования
     * @throws AuthorizeException Если пользователь не авторизован
     */
    @Operation(summary = "Получение всех мест для бронирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех мест для бронирования"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping
    public ResponseEntity<List<ReservationPlaceResponseDTO>> getAllReservationPlaces(
            @RequestHeader("Authorization") String authHeader) throws AuthorizeException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        List<ReservationPlace> reservationPlaceList = reservationPlaceService.getAllReservationPlaces();

        return ResponseEntity.ok(reservationPlaceMapper.toResponseDTOList(reservationPlaceList));
    }

    /**
     * Получение всех доступных дат для бронирования места по id
     * @param reservationPlaceId Уникальный идентификатор места для бронирования
     * @param authHeader Заголовок с токеном авторизации
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
             @RequestHeader("Authorization") String authHeader) throws ObjectNotFoundException, AuthorizeException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
        if(reservationPlace == null) {
               throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
        }
        List<LocalDate> availableDatesForReservePlace =
                freeReservationSlotService.getAllAvailableDatesForReservePlace(reservationPlace);
        ReservationPlaceDateSlotResponseDTO reservationPlaceDateSlotResponseDTO =
                reservationPlaceMapper.toFreeSlotResponseDTO(reservationPlace, availableDatesForReservePlace);
        return ResponseEntity.ok(reservationPlaceDateSlotResponseDTO);
    }

    /**
     * Получение всех доступных дат для бронирования всех мест
     * @param authHeader Заголовок с токеном авторизации
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
            (@RequestHeader("Authorization") String authHeader) throws AuthorizeException {
        jwtUtil.verifyTokenFromHeader(authHeader);
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
    }

    /**
     * Получение всех доступных мест для бронирования на указанную дату
     * @param localDateRequest Объект содержащий желаемую дату для бронирования мест
     * @param authHeader Заголовок с токеном авторизации
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
             @RequestHeader("Authorization") String authHeader) throws AuthorizeException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        List<ReservationPlace> availableReservationPlaces =
                freeReservationSlotService.getAllAvailablePlacesForReserveDate(localDateRequest.getLocalDate());
        if(availableReservationPlaces.isEmpty()) {
            throw new ObjectNotFoundException(
                    "Place for reservation on date " + localDateRequest.getLocalDate() + " does not exists");
        } else {
            return ResponseEntity.ok(availableReservationPlaces);
        }
    }

    /**
     * Получение всех доступных временных промежутков в указанную дату для бронирования места по id
     * @param reservationPlaceId Уникальный идентификатор места для бронирования
     * @param localDateRequest Объект содержащий желаемую дату для бронирования мест
     * @param authHeader Заголовок с токеном авторизации
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
             @RequestHeader("Authorization") String authHeader) throws AuthorizeException, ObjectNotFoundException {
        jwtUtil.verifyTokenFromHeader(authHeader);
        ReservationPlace reservationPlace = reservationPlaceService.getReservationPlace(reservationPlaceId);
        if(reservationPlace == null) {
            throw new ObjectNotFoundException("Place with id " + reservationPlaceId + " does not exists");
        }
        return ResponseEntity.ok(freeReservationSlotService
                .getAllAvailableTimeListForReservation(reservationPlace, localDateRequest.getLocalDate()));
    }
}
