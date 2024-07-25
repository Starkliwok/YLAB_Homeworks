package com.Y_LAB.homework.testcontainers;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    public Connection connection(HikariDataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }
}
