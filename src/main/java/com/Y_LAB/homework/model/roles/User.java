package com.Y_LAB.homework.model.roles;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс пользователя приложения
 * @author Денис Попов
 * @version 2.0
 */
@Data
@AllArgsConstructor
public class User {

    /** Поле уникального идентификатора пользователя.*/
    private final long id;

    /** Поле уникального логина пользователя, используется при входе в систему.*/
    private String username;

    /** Поле пароля пользователя, используется при входе в систему.*/
    private String password;
}
