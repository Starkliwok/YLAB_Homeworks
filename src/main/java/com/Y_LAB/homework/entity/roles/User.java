package com.Y_LAB.homework.entity.roles;

import lombok.*;

/**
 * Класс пользователя приложения
 * @author Денис Попов
 * @version 2.0
 */
@Data
@AllArgsConstructor
public class User {

    /** Поле уникального идентификатора, которое задается при создании объекта, это поле невозможно изменить.*/
    private final long id;

    /** Поле уникального логина пользователя, используется при входе в систему.*/
    private String username;

    /** Поле пароля пользователя, используется при входе в систему.*/
    private String password;

    @Override
    public String toString() {
        return "\nПользователь" +
                "\nid = " + id +
                "\nИмя = '" + username + '\'' +
                "\nПароль = '" + password + '\'';
    }
}
