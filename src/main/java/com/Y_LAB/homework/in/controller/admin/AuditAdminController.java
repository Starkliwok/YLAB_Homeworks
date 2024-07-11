package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.mapper.AuditMapper;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.AuditResponseDTO;
import com.Y_LAB.homework.service.AuditService;
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
 * Сервлет панели администратора для управления аудитами
 * @author Денис Попов
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH)
public class AuditAdminController {

    private final AuditMapper auditMapper;

    private final AuditService auditService;

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
