package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.AuditDAO;
import com.Y_LAB.homework.dao.implementation.AuditDAOImpl;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class AuditDAOImplTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = TestcontrainerManager.getPostgreSQLContainer();

    private static AuditDAO auditDAO;

    private final Audit audit = new Audit(1, 6L, LocalDateTime.of(2020, 1, 10, 1, 0), "someAction");

    @BeforeAll
    static void beforeAll() {
        Properties properties = new Properties();
        properties.setProperty(PROPERTIES_URL_KEY, postgresContainer.getJdbcUrl());
        properties.setProperty(PROPERTIES_USERNAME_KEY, postgresContainer.getUsername());
        properties.setProperty(PROPERTIES_PASSWORD_KEY, postgresContainer.getPassword());
        LiquibaseMigration.initMigration(ConnectionToDatabase.getConnectionFromProperties(properties));
        Connection connection = ConnectionToDatabase.getConnectionFromProperties(properties);

        auditDAO = new AuditDAOImpl(connection);
    }

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
        Audit audit1 = auditDAO.getAllAudits().stream().filter(x -> x.getUserId() == 6L && x.getAction().equals("someAction")).findAny().orElse(null);

        assert audit1 != null;
        Audit actual = auditDAO.getAudit(audit1.getId());

        assertThat(actual).isNotNull();
        assertThat(actual.getLocalDateTime()).isEqualTo(audit.getLocalDateTime());
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