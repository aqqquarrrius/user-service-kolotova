package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserCreateRequest;
import com.example.userservice.dto.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserCreateRequest request);
    Optional<UserDto> getUserById(Long id);
    List<UserDto> getAllUsers();
    Optional<UserDto> updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}






