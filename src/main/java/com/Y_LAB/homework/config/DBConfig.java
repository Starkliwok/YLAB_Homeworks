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

import static com.Y_LAB.homework.dao.constants.SQLConstants.CREATE_COWORKING_SERVICE_SCHEMA_SQL;

/**
 * Конфигурация базы данных
 * @author Денис Попов
 * @version 1.0
 */
@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertyFactory.class)
public class DBConfig {

    /**Поле драйвера БД*/
    @Value("${db.classname}")
    private String driverClassName;

    /**Поле URL БД*/
    @Value("${db.url}")
    private String url;

    /**Поле логина к БД*/
    @Value("${db.username}")
    private String username;

    /**Поле пароля к БД*/
    @Value("${db.password}")
    private String password;

    /**Местоположение changelog файла Liquibase*/
    @Value("${migration.change_log_file}")
    private String pathChangeLog;

    /**Название служебной схемы для хранения служебных таблиц Liquibase*/
    @Value("${migration.service_schema}")
    private String serviceSchema;

    /**Создает и настраивает объект {@link DriverManagerDataSource} для получения подключения к базе данных*/
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
    }

    /**
     * Создает и настраивает объект {@link SpringLiquibase} для исполнения миграций БД,
     * перед исполнением миграций БД создает служебную схему для хранения служебных таблиц Liquibase
     */
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
