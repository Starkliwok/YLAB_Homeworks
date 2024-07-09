package com.Y_LAB.homework.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class TestcontrainerManager {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withExposedPorts(5433, 5432)
            .withUsername("postgres")
            .withPassword("starkliw")
            .withDatabaseName("postgres");

    private TestcontrainerManager() {}

    public static PostgreSQLContainer<?> getPostgreSQLContainer() {
        return postgresContainer;
    }
}
