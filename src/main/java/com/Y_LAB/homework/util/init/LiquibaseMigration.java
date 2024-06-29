package com.Y_LAB.homework.util.init;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.PROPERTIES_CHANGE_LOG_FILE_KEY;
import static com.Y_LAB.homework.constants.ApplicationConstants.PROPERTIES_PATH;

/**
 * Класс для выполнения миграций Liquibase
 * @author Денис Попов
 * @version 1.0
 */
public class LiquibaseMigration {

    private LiquibaseMigration() {}

    /**
     * Метод конфигурирует миграции баз данных, предварительно создает таблицу coworking_service для хранения
     * служебных таблиц Liquibase
     * @param connection объект, по которому будет происходить подключение к базе данных
     */
    public static void initMigration(Connection connection) {
        try {
            Properties properties = PropertiesLoader.getProperties(PROPERTIES_PATH);
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA IF NOT EXISTS coworking_service");
            statement.close();
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase(properties.getProperty(PROPERTIES_CHANGE_LOG_FILE_KEY), new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            connection.close();
        } catch (LiquibaseException | SQLException e) {
            System.out.println("Произошла ошибка, приложение завершает работу");
            System.exit(-1);
        }
    }
}
