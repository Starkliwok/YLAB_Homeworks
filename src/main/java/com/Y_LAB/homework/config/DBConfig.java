package com.Y_LAB.homework.config;

import com.Y_LAB.homework.util.db.YamlPropertyFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertyFactory.class)
public class DBConfig {

    @Value("${db.classname}")
    private String driverClassName;

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${migration.change_log_file}")
    private String pathChangeLog;

    @Value("${migration.service_schema}")
    private String serviceSchema;

    private static final String CREATE_COWORKING_SERVICE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS coworking_service";

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
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
