package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.aop.model.audit.Audit;
import com.Y_LAB.homework.aop.model.dto.response.AuditResponseDTO;
import com.Y_LAB.homework.aop.service.AuditService;
import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.exception.auth.NotEnoughRightsException;
import com.Y_LAB.homework.mapper.AuditMapper;
import com.Y_LAB.homework.security.JwtUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_AUDIT_PATH;

/**
 * REST контроллер панели администратора для управления аудитами
 * @author Денис Попов
 * @version 1.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Coworking Service",
                contact = @io.swagger.v3.oas.annotations.info.Contact(name = "D.Popov", email = "starkliw@yandex.ru"),
                summary = "Приложение для бронирования мест",
                description = "Приложение позволяет пользователям выбирать места для бронирования и бронировать помещения на доступные даты",
                version = "1.0"
        )
)
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH)
@Tag(name = "(ADMIN) Аудиты пользователей")
public class AuditAdminController {

    /** Поле для преобразования DTO объектов аудита*/
    private final AuditMapper auditMapper;

    /** Поле сервиса аудитов*/
    private final AuditService auditService;

    /** Поле для валидации входящих токенов авторизации*/
    private final JwtUtil jwtUtil;

    /**
     * Получение аудита по id
     * @param auditId уникальный идентификатор аудита
     * @param authHeader Заголовок с токеном авторизации
     * @return Аудит
     * @throws ObjectNotFoundException Если аудита с таким id не существует
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение аудита по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аудит найден"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором"),
            @ApiResponse(responseCode = "404", description = "Аудит с указанным id не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditResponseDTO> getAudit(
            @PathVariable("id") Long auditId,
            @RequestHeader("Authorization") String authHeader) throws ObjectNotFoundException, AuthorizeException, NotEnoughRightsException {
        jwtUtil.verifyAdminTokenFromHeader(authHeader);
        Audit audit = auditService.getAudit(auditId);
        if (audit != null) {
            AuditResponseDTO auditResponseDTO = auditMapper.toAuditResponseDTO(audit);
            return ResponseEntity.ok(auditResponseDTO);
        } else {
            throw new ObjectNotFoundException("Audit with id " + auditId + " does not exists");
        }
    }

    /**
     * Получение всех аудитов пользователя
     * @param authHeader Заголовок с токеном авторизации
     * @return Список всех аудитов
     * @throws AuthorizeException Если пользователь не авторизован
     * @throws ClassCastException Если пользователь не является администратором
     */
    @Operation(summary = "Получение всех аудитов пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возращение всех аудитов"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация"),
            @ApiResponse(responseCode = "403", description = "Пользователь авторизован, но не является администратором")
    })
    @GetMapping
    public ResponseEntity<List<AuditResponseDTO>> getAllAudits(
            @RequestHeader("Authorization") String authHeader) throws AuthorizeException, NotEnoughRightsException {
        jwtUtil.verifyAdminTokenFromHeader(authHeader);
        List<Audit> audits = auditService.getAllAudits();
        List<AuditResponseDTO> auditResponseDTOS = auditMapper.toAuditResponseDTOList(audits);

        return ResponseEntity.ok(auditResponseDTOS);
    }
}
