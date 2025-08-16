package com.example.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void createUser_validData_userCreated() {
        String name = "Dmitrii";
        String email = "dmitrii@example.com";
        Integer age = 28;
        User user = new User(name, email, age);

        when(userDao.create(any(User.class))).thenReturn(user);

        userService.createUser(name, email, age);

        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void createUser_invalidName_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("", "test@example.com", 20);
        });
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    void createUser_invalidEmail_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("Dima", "dimychexample.com", 20);
        });
        assertEquals("Invalid email", exception.getMessage());
    }

    @Test
    void createUser_negativeAge_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("Dima", "dimych@example.com", -42);
        });
        assertEquals("Age cannot be negative", exception.getMessage());
    }
}
