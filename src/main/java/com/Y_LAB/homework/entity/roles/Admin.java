package com.Y_LAB.homework.entity.roles;


/**
 * Класс администратора обладает правами как обычного {@link User}, так и правами по удалению, изменению данных других
 * пользователей, связанных с их данными от аккаунта или бронированием помещений
 * @author Денис Попов
 * @version 1.0
 */
public class Admin extends User {

    /**
     * Конструктор, который принимает параметры и передает их в родительский класс
     * @param username логин
     * @param password пароль
     */
    public Admin(String username, String password) {
        super(username, password);
    }
}
