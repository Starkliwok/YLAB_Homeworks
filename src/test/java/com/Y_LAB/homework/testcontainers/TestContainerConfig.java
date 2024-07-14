package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.AuditDAO;
import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.dao.implementation.AuditDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.service.FreeReservationSlotService;
import com.Y_LAB.homework.service.implementation.FreeReservationSlotServiceImpl;
import com.Y_LAB.homework.util.db.YamlPropertyFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@Testcontainers
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertyFactory.class)
public class TestContainerConfig {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withExposedPorts(5433, 5432)
            .withUsername("postgres")
            .withPassword("starkliw")
            .withDatabaseName("postgres");

    @Value("${db.classname}")
    private String driverClassName;

    @Value("${migration.change_log_file}")
    private String pathChangeLog;

    @Value("${migration.service_schema}")
    private String serviceSchema;

    private static final String CREATE_COWORKING_SERVICE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS coworking_service";

    static {
        postgresContainer.start();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());
        dataSource.setUrl(postgresContainer.getJdbcUrl());
        return dataSource;
    }

    @Bean
    public AuditDAO auditDAO() throws SQLException {
        return new AuditDAOImpl(dataSource());
    }

    @Bean
    public ReservationPlaceDAO reservationPlaceDAO() throws SQLException {
        return new ReservationPlaceDAOImpl(dataSource());
    }

    @Bean
    public ReservationDAO reservationDAO() throws SQLException {
        return new ReservationDAOImpl(dataSource(), reservationPlaceDAO());
    }

    @Bean
    public UserDAO userDAO() throws SQLException {
        return new UserDAOImpl(dataSource());
    }

    @Bean
    public FreeReservationSlotService freeReservationSlotService() throws SQLException {
        return new FreeReservationSlotServiceImpl(reservationPlaceDAO(), reservationDAO());
    }

    @Bean
    public SpringLiquibase liquibase() {
        try (Connection connection = dataSource().getConnection();
             Statement statement = connection.createStatement()){
            statement.execute(CREATE_COWORKING_SERVICE_SCHEMA_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(pathChangeLog);
        liquibase.setDataSource(dataSource());
        liquibase.setLiquibaseSchema(serviceSchema);
        return liquibase;
    }
}
