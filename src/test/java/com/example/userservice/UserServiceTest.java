package com.example.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final String TEST_NAME = "Dmitrii";
    private static final String TEST_EMAIL = "dmitrii@example.com";
    private static final int TEST_AGE = 27;

    private static final String UPDATED_NAME = "Anton";
    private static final String UPDATED_EMAIL = "anton@example.com";


    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    private User newUser(String name, String email, Integer age) {
        return new User(name, email, age);
    }

//    createUser
    @Test
    void createUser_validData_userCreated() {
        when(userDao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User result = userService.createUser(TEST_NAME, TEST_EMAIL, TEST_AGE);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(TEST_NAME, result.getName()),
                () -> assertEquals(TEST_EMAIL, result.getEmail()),
                () -> assertEquals(TEST_AGE, result.getAge())
        );

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao, times(1)).create(captor.capture());
        User passed = captor.getValue();

        assertAll(
                () -> assertEquals(TEST_NAME, result.getName()),
                () -> assertEquals(TEST_EMAIL, result.getEmail()),
                () -> assertEquals(TEST_AGE, result.getAge())
        );
    }

    @Test
    void createUser_invalidName_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.createUser("", TEST_EMAIL, TEST_AGE));
        assertEquals("Name cannot be empty", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_invalidEmail_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(TEST_NAME, "invalid email", TEST_AGE));
        assertEquals("Invalid email", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_negativeAge_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(TEST_NAME, TEST_EMAIL, -42));
        assertEquals("Age cannot be negative", exception.getMessage());
        verifyNoInteractions(userDao);
    }

//    getUserById
    @Test
    void getUserById_success() {
        User user = newUser(TEST_NAME, TEST_EMAIL, TEST_AGE);
        when(userDao.getById(1L)).thenReturn(Optional.of(user));

        Optional<User> found = userService.getUserById(1L);

        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertEquals(TEST_NAME, found.get().getName()),
                () -> assertEquals(TEST_EMAIL, found.get().getEmail()),
                () -> assertEquals(TEST_AGE, found.get().getAge())
        );
    }

    @Test
    void getUserById_invalidId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(0L));
        verifyNoInteractions(userDao);
    }


//    getAllUsers
    @Test
    void getAllUsers_success() {
        List<User> users = Arrays.asList(
                newUser(TEST_NAME, TEST_EMAIL, TEST_AGE),
                newUser(UPDATED_NAME, UPDATED_EMAIL, TEST_AGE)
        );
        when(userDao.getAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(TEST_NAME, result.get(0).getName()),
                () -> assertEquals(UPDATED_NAME, result.get(1).getName())
        );
    }

//    updateUser
    @Test
    void updateUser_success() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(UPDATED_EMAIL);
        when(user.getAge()).thenReturn(TEST_AGE);
        when(user.getName()).thenReturn(UPDATED_NAME);

        userService.updateUser(user);

        verify(userDao).update(user);
    }

    @Test
    void updateUser_null_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.updateUser(null));
        assertEquals("User must not be null", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void updateUser_missingId_ThrowsException() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(user));
        assertEquals("User ID is required for update", exception.getMessage());
        verifyNoInteractions(userDao);
    }

//    deleteUser
    @Test
    void deleteUser_success() {
        userService.deleteUser(1L);
        verify(userDao).delete(1L);
    }

    @Test
    void deleteUser_invalidId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(0L));
        verifyNoInteractions(userDao);
    }
}


