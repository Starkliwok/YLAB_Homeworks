package com.Y_LAB.homework.entity.roles;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс пользователя приложения
 * @author Денис Попов
 * @version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
public class User {

    /** Статическое поле количества пользователей, используется для инициализации поля {@link User#id}.*/
    @EqualsAndHashCode.Exclude
    private static int usersCount;

    /** Поле уникального идентификатора, которое задается при создании объекта, это поле невозможно изменить.*/
    @EqualsAndHashCode.Exclude
    private final int id;

    /** Поле уникального логина пользователя, используется при входе в систему.*/
    private String username;

    /** Поле пароля пользователя, используется при входе в систему.*/
    private String password;

    /**
     * Конструктор, который принимает параметры и инициализирует поля класса,
     * поле {@link User#id задается по количеству всех аккаунтов на данный момент}
     * @param username логин
     * @param password пароль
     */
    public User(String username, String password) {
        id = ++usersCount;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "\nПользователь" +
                "\nid = " + id +
                "\nИмя = '" + username + '\'' +
                "\nПароль = '" + password + '\'';
    }
}
