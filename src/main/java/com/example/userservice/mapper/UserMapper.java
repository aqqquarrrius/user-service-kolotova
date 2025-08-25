package com.example.userservice.mapper;


import com.example.userservice.model.User;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserCreateRequest;
import com.example.userservice.dto.UserUpdateRequest;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    public static User toEntity(UserCreateRequest request) {
        return new User(
                request.getName(),
                request.getEmail(),
                request.getAge()
        );
    }

    public static void updateEntity(User user, UserUpdateRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
    }
}
