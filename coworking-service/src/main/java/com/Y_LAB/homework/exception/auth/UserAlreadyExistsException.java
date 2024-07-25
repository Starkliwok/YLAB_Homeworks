package com.Y_LAB.homework.exception.auth;

/**
 * Класс наследуется от {@link RegistrationException} и описывает невозможность создания или изменения уникального логина пользователя,
 * на тот логин который уже занят другим пользователем
 * @author Денис Попов
 * @version 2.0
 */
public class UserAlreadyExistsException extends RegistrationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
