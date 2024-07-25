package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.aop.dao.AuditDAO;
import com.Y_LAB.homework.aop.model.audit.Audit;
import com.Y_LAB.homework.aop.model.audit.AuditResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestContainerConfig.class})
class AuditDAOImplTest {

    @Autowired
    private AuditDAO auditDAO;

    private final Audit audit = new Audit(10000L, 6L,
            LocalDateTime.of(2020, 1, 10, 1, 0),
            "com.test.test", "test()", AuditResult.FAIL);

    @Test
    @DisplayName("Получение всех аудитов из бд")
    void getAllAudits() {
        int expected = auditDAO.getAllAudits().size() + 1;
        auditDAO.saveAudit(audit);

        int actual = auditDAO.getAllAudits().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение аудита по id из бд")
    void getAudit() {
        long expected = 1L;
        auditDAO.saveAudit(audit);

        long actual = auditDAO.getAudit(expected).getId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Сохранение аудита в бд")
    void saveAudit() {
        int expected = auditDAO.getAllAudits().size() + 1;

        auditDAO.saveAudit(audit);
        int actual = auditDAO.getAllAudits().size();

        assertThat(actual).isEqualTo(expected);
    }
}