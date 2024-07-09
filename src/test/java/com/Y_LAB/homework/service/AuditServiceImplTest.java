package com.Y_LAB.homework.service;

import com.Y_LAB.homework.dao.implementation.AuditDAOImpl;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.service.implementation.AuditServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditDAOImpl auditDAO;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    @DisplayName("Проверка на вызов метода получения всех аудитов")
    void getAllAudits() {
        auditService.getAllAudits();

        verify(auditDAO, times(1)).getAllAudits();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения аудита по id")
    void getAudit() {
        long id = 1;

        auditService.getAudit(id);

        verify(auditDAO, times(1)).getAudit(id);
    }

    @Test
    @DisplayName("Проверка на вызов метода сохранения аудита")
    void saveAudit() {
        Audit audit = new Audit(1L, "ds");

        auditService.saveAudit(audit);

        verify(auditDAO, times(1)).saveAudit(audit);
    }
}