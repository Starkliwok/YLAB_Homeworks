package com.Y_LAB.homework.model.roles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void init() {
        user = new User(1, "user", "password");
    }

    @Test
    @DisplayName("Получение уникального идентификатора")
    void getId() {
        long expected = 1;
        long actual = user.getId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение логина")
    void getUsername() {
        user.setUsername("user2");

        String expected = "user2";
        String actual = user.getUsername();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение пароля")
    void getPassword() {
        user.setPassword("password2");

        String expected = "password2";
        String actual = user.getPassword();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение логина")
    void setUsername() {
        String expected = user.getUsername() + "2";

        user.setUsername(expected);
        String actual = user.getUsername();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Изменение пароля")
    void setPassword() {
        String expected = user.getPassword() + "2";

        user.setPassword(expected);
        String actual = user.getPassword();

        assertThat(actual).isEqualTo(expected);
    }
}