package com.example.userservice;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    void update(User user);

    void delete(Long id);
}
