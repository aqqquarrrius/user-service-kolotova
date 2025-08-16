package com.example.userservice;

import com.example.userservice.User;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if(email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if(age != null && age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }

        User user = new User(name, email, age);
        return userDao.create(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return userDao.getById(id);
    }

    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    public void updateUser(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("User ID is required for update");
        }
        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (user.getAge() !=null && user.getAge() < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        userDao.update(user);
    }

    public void deleteUser(Long id) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        userDao.delete(id);
    }

}
