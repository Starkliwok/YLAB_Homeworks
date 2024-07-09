package com.Y_LAB.homework.util.reservation.init;

import com.Y_LAB.homework.util.init.PropertiesLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.Properties;

import static com.Y_LAB.homework.constants.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

class PropertiesLoaderTest {

    @Test
    @DisplayName("Получение файла .properties по пути к файлу, проверка содержимых данных в этом файле на корректность")
    @SneakyThrows
    void getProperties() {
        Properties expected = new Properties();
        expected.load(new FileInputStream("src/main/resources/application.properties"));

        Properties actual = PropertiesLoader.getProperties("application.properties");

        assertThat(actual.isEmpty()).isEqualTo(false);
        assertThat(actual.get(PROPERTIES_URL_KEY)).isEqualTo(expected.get(PROPERTIES_URL_KEY));
        assertThat(actual.get(PROPERTIES_USERNAME_KEY)).isEqualTo(expected.get(PROPERTIES_USERNAME_KEY));
        assertThat(actual.get(PROPERTIES_PASSWORD_KEY)).isEqualTo(expected.get(PROPERTIES_PASSWORD_KEY));
        assertThat(actual.get(PROPERTIES_CHANGE_LOG_FILE_KEY)).isEqualTo(expected.get(PROPERTIES_CHANGE_LOG_FILE_KEY));
    }
}