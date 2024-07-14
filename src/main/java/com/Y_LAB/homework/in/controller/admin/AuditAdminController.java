package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.AuditMapper;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.AuditResponseDTO;
import com.Y_LAB.homework.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_AUDIT_PATH;

/**
 * REST контроллер панели администратора для управления аудитами
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH)
@Tag(name = "(ADMIN) Аудиты пользователей")
public class AuditAdminController {

    /** Поле для преобразования DTO объектов аудита*/
    private final AuditMapper auditMapper;

    /** Поле сервиса аудитов*/
    private final AuditService auditService;


    /**
     * Получение аудита по id
     * @param auditId уникальный идентификатор аудита
     * @param req Http запрос для получения сессии пользователя
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
    public ResponseEntity<AuditResponseDTO> getAudit(@PathVariable("id") Long auditId,
                                      HttpServletRequest req) throws ObjectNotFoundException, AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            Audit audit = auditService.getAudit(auditId);
            if (audit != null) {
                AuditResponseDTO auditResponseDTO = auditMapper.toAuditResponseDTO(audit);
                return ResponseEntity.ok(auditResponseDTO);
            } else {
                throw new ObjectNotFoundException("Audit with id " + auditId + " does not exists");
            }
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }

    /**
     * Получение всех аудитов пользователя
     * @param req Http запрос для получения сессии пользователя
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
    public ResponseEntity<List<AuditResponseDTO>> getAllAudits(HttpServletRequest req) throws AuthorizeException {
        HttpSession session = req.getSession();
        AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
        if (adminRequestDTO != null) {
            List<Audit> audits = auditService.getAllAudits();
            List<AuditResponseDTO> auditResponseDTOS = auditMapper.toAuditResponseDTOList(audits);
            return ResponseEntity.ok(auditResponseDTOS);
        } else {
            throw new AuthorizeException("You are not logged in");
        }
    }
}
