package com.Y_LAB.homework.in.controller.admin;

import com.Y_LAB.homework.config.WebConfig;
import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.in.controller.ControllerExceptionHandler;
import com.Y_LAB.homework.mapper.AuditMapper;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.audit.AuditResult;
import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.SESSION_USER;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_ADMIN_PATH;
import static com.Y_LAB.homework.in.controller.constants.ControllerPathConstants.CONTROLLER_AUDIT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = WebConfig.class)
class AuditAdminControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AuditAdminController auditAdminController;

    @Mock
    private AuditService auditService;

    @Mock
    private AuditMapper auditMapper;

    private final AdminRequestDTO adminRequestDTO = new AdminRequestDTO();

    private final MockHttpSession session = new MockHttpSession();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(auditAdminController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Получение аудита, код 403 когда пользователь не является администратором")
    void getAudit_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());
        long auditId = 1L;

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH + "/{id}", auditId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение аудита, код 404 когда такого аудита не существует")
    void getAudit_NotFound() throws Exception {
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long auditId = 1L;
        when(auditService.getAudit(auditId)).thenReturn(null);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH + "/{id}", auditId).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .isEqualTo("Audit with id " + auditId + " does not exists"));

        verify(auditService, times(1)).getAudit(auditId);
        verifyNoMoreInteractions(auditService);
    }

    @Test
    @DisplayName("Получение аудита, код 200")
    void getAudit() throws Exception {
        Audit mockAudit = new Audit(1, 1L, LocalDateTime.now(), "2", "a", AuditResult.SUCCESS);
        session.setAttribute(SESSION_USER, adminRequestDTO);
        long auditId = 1L;
        when(auditService.getAudit(auditId)).thenReturn(mockAudit);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH + "/{id}", auditId).session(session))
                .andExpect(status().isOk());

        verify(auditService, times(1)).getAudit(auditId);
        verify(auditMapper, times(1)).toAuditResponseDTO(mockAudit);
        verifyNoMoreInteractions(auditService);
        verifyNoMoreInteractions(auditMapper);
    }

    @Test
    @DisplayName("Получение аудита, код 401 когда пользователь не авторизован")
    void getAudit_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH + "/{id}", 2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }

    @Test
    @DisplayName("Получение всех аудитов, код 200")
    void getAllAudits() throws Exception {
        List<Audit> mockAudits = new ArrayList<>();
        session.setAttribute(SESSION_USER, adminRequestDTO);
        when(auditService.getAllAudits()).thenReturn(mockAudits);

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(auditService, times(1)).getAllAudits();
        verify(auditMapper, times(1)).toAuditResponseDTOList(mockAudits);
        verifyNoMoreInteractions(auditService);
        verifyNoMoreInteractions(auditMapper);
    }

    @Test
    @DisplayName("Получение всех аудитов, код 403 когда пользователь не является администратором")
    void getAllAudits_NotAdmin() throws Exception {
        session.setAttribute(SESSION_USER, new UserRequestDTO());

        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH).session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertInstanceOf(ClassCastException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("Получение всех аудитов, код 401 когда пользователь не авторизован")
    void getAllAudits_UnAuthorized() throws Exception {
        mockMvc.perform(get(CONTROLLER_ADMIN_PATH + CONTROLLER_AUDIT_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(AuthorizeException.class, result.getResolvedException()))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage()).isEqualTo("You are not logged in"));
    }
}