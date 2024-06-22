package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link Exception} и описывает невозможность создания или изменения уникального логина пользователя,
 * на тот логин который уже занят другим пользователем
 * @author Денис Попов
 * @version 1.0
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
