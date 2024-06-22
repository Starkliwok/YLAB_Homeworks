package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.entity.roles.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAOImpl userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Вызов метода получения всех пользователей")
    void getUserSet() {
        userService.getUserSet();

        verify(userDAO, times(1)).getUserSet();
    }

    @Test
    @DisplayName("Вызов метода получения всех логинов")
    void getUsernameSet() {
        userService.getUsernameSet();

        verify(userDAO, times(1)).getUsernameSet();
    }

    @Test
    @DisplayName("Вызов метода получения всех логинов и паролей")
    void getAllReservedUsernamesAndPasswords() {
        userService.getAllReservedUsernamesAndPasswords();

        verify(userDAO, times(1)).getAllReservedUsernamesAndPasswords();
    }

    @Test
    @DisplayName("Вызов метода получения пользователя")
    void getUserFromUserSet() {
        String username = "user";
        String password = "password";

        userService.getUserFromUserSet(username, password);

        verify(userDAO, times(1)).getUserFromUserSet(username, password);
    }

    @Test
    @DisplayName("Вызов метода добавления пользователя")
    void addUserToUserSet() {
        User user = new User("user", "password");

        userService.addUserToUserSet(user);

        verify(userDAO, times(1)).addUserToUserSet(user);
    }

    @Test
    @DisplayName("Вызов метода обновления логина пользователя")
    void updateUsername() {
        User user = new User("user", "password");
        String newUsername = "someUsername";

        userService.updateUsername(user, newUsername);

        verify(userDAO, times(1)).updateUsername(user, newUsername);
    }

    @Test
    @DisplayName("Вызов метода обновления пароля пользователя")
    void updateUserPassword() {
        User user = new User("user", "password");
        String newPassword = "somePassword";

        userService.updateUserPassword(user, newPassword);

        verify(userDAO, times(1)).updateUserPassword(user, newPassword);
    }

    @Test
    @DisplayName("Вызов метода удаления пользователя")
    void deleteUser() {
        User user = new User("user", "password");

        userService.deleteUser(user);

        verify(userDAO, times(1)).deleteUser(user);
    }
}