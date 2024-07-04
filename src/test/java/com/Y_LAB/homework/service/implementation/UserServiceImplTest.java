package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.exception.user.auth.RegistrationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAOImpl userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Проверка на вызов метода получения всех пользователей")
    void getAllUsers() {
        userService.getAllUsers();

        verify(userDAO, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Проверка на вызов метода получения пользователя по id")
    void getUserWithID() {
        int id = 1;

        userService.getUser(id);

        verify(userDAO, times(1)).getUser(id);
    }

    @Test
    @DisplayName("Проверка на вызов метода получения пользователя по логину и паролю")
    void getUserWithUsernameAndPassword() {
        String username = "user";
        String password = "password";

        userService.getUser(username, password);

        verify(userDAO, times(1)).getUser(username, password);
    }

    @Test
    @DisplayName("Проверка на вызов метода сохранения пользователя")
    @SneakyThrows
    void shouldSaveUser() {
        String username = "user";
        String password = "password";

        userService.saveUser(username, password);

        verify(userDAO, times(1)).saveUser(username, password);
    }

    @Test
    @DisplayName("Не должен сохранить пользователя с паролем, который меньше минимальной длины пароля")
    @SneakyThrows
    void shouldNotSaveUser_PasswordMinLength_ReturnAuthorizationException() {
        String username = "user432";
        String password = "2";

        assertThrows(RegistrationException.class, () -> userService.saveUser(username, password));
    }

    @Test
    @DisplayName("Не должен сохранить пользователя с паролем, который больше максимальной длины пароля")
    @SneakyThrows
    void shouldNotSaveUser_PasswordMaxLength_ReturnAuthorizationException() {
        String username = "user432";
        String password = "0123456789123456789012345678901";

        assertThrows(RegistrationException.class, () -> userService.saveUser(username, password));
    }

    @Test
    @DisplayName("Не должен сохранить пользователя с логином, который меньше минимальной длины логина")
    @SneakyThrows
    void shouldNotSaveUser_UsernameMinLength_ReturnAuthorizationException() {
        String username = "2";
        String password = "password";

        assertThrows(RegistrationException.class, () -> userService.saveUser(username, password));
    }

    @Test
    @DisplayName("Не должен сохранить пользователя с логином, который больше максимальной длины логина")
    @SneakyThrows
    void shouldNotSaveUser_UsernameMaxLength_ReturnAuthorizationException() {
        String username = "0123456789123456789012345678901";
        String password = "password";

        assertThrows(RegistrationException.class, () -> userService.saveUser(username, password));
    }

    @Test
    @DisplayName("Не должен сохранить пользователя, который уже существует в базе данных")
    @SneakyThrows
    void shouldNotSaveUser_UserAlreadyExists_ReturnAuthorizationException() {
        String username = "root";
        String password = "root";

        assertThrows(RegistrationException.class, () -> userService.saveUser(username, password));
    }

    @Test
    @DisplayName("Проверка на вызов метода обновления пользователя")
    void updateUser() {
        int id = 1;
        String username = "user";
        String password = "password";
        User user = new User(id, username, password);

        userService.updateUser(user);

        verify(userDAO, times(1)).updateUser(user);
    }

    @Test
    @DisplayName("Проверка на вызов метода удаления пользователя")
    void deleteUser() {
        int id = 1;

        userService.deleteUser(id);

        verify(userDAO, times(1)).deleteUser(id);
    }

    @Test
    @DisplayName("Проверка на вызов метода проверки на существование пользователя")
    void isUserExist() {
        String username = "user";

        userService.isUserExist(username);

        verify(userDAO, times(1)).isUserExist(username);
    }
}