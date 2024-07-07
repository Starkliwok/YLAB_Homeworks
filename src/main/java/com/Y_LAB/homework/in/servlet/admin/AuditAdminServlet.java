package com.Y_LAB.homework.in.servlet.admin;

import com.Y_LAB.homework.exception.model.ErrorResponse;
import com.Y_LAB.homework.in.util.ServletPathUtil;
import com.Y_LAB.homework.mapper.AuditMapper;
import com.Y_LAB.homework.mapper.AuditMapperImpl;
import com.Y_LAB.homework.mapper.UserMapper;
import com.Y_LAB.homework.mapper.UserMapperImpl;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.response.AuditResponseDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.AuditService;
import com.Y_LAB.homework.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.CONTENT_JSON;
import static com.Y_LAB.homework.in.servlet.constants.ControllerConstants.ENCODING;
import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.servlet.constants.ControllerPathConstants.CONTROLLER_AUDIT_PATH;

@WebServlet(urlPatterns = {CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH
        , CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH + "/*"})
public class AuditAdminServlet extends HttpServlet {

    private final AuditMapper auditMapper;

    private ObjectMapper objectMapper;

    private AuditService auditService;

    public AuditAdminServlet() {
        auditMapper = new AuditMapperImpl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        auditService = (AuditService) getServletContext().getAttribute(AUDIT_SERVICE);
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        resp.setContentType(CONTENT_JSON);
        resp.setCharacterEncoding(ENCODING);
        String path = ServletPathUtil.getPath(req);
        try {
            AdminRequestDTO adminRequestDTO = (AdminRequestDTO) session.getAttribute(SESSION_USER);
            if (adminRequestDTO != null) {
                if (path != null) {
                    int auditId = Integer.parseInt(path);
                    Audit audit = auditService.getAudit(auditId);
                    if (audit != null) {
                        AuditResponseDTO auditResponseDTO = auditMapper.toAuditResponseDTO(audit);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(objectMapper.writeValueAsString(auditResponseDTO));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(objectMapper.writeValueAsString(
                                new ErrorResponse("Аудита с таким идентификатором не существует")));
                    }
                } else {
                    List<Audit> audits = auditService.getAllAudits();
                    List<AuditResponseDTO> auditResponseDTOS = auditMapper.toAuditResponseDTOList(audits);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(auditResponseDTOS));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Пользователь не авторизован")));
            }
        } catch (ClassCastException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Нет прав")));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Идентификатор должен состоять из чисел")));
        }
    }
}
