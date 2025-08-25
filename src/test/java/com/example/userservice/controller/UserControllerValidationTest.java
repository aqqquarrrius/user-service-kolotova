package com.example.userservice.controller;

import com.example.userservice.controller.UserController;
import com.example.userservice.dto.UserUpdateRequest;
import com.example.userservice.dto.UserCreateRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserCreateRequest validCreateRequest;
    private UserUpdateRequest validUpdateRequest;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        validCreateRequest = new UserCreateRequest("Alice", "alice@example.com", 25);
        validUpdateRequest = new UserUpdateRequest("AliceUpdated", "aliceu@example.com", 26);
    }

    @Test
    void shouldReturn200WhenCreateUserWithValidData() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(new UserDto(1L, validCreateRequest.getName(),
                        validCreateRequest.getEmail(),
                        validCreateRequest.getAge(),
                        LocalDateTime.now()));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(validCreateRequest.getName()))
                .andExpect(jsonPath("$.email").value(validCreateRequest.getEmail()));
    }

    @Test
    void shouldReturn400WhenCreateUserWithInvalidData() throws Exception {
        UserCreateRequest invalidRequest = new UserCreateRequest("", "invalid-email", -5);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenUpdateUserWithValidData() throws Exception {
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
                .thenReturn(Optional.of(new UserDto(1L, validUpdateRequest.getName(),
                        validUpdateRequest.getEmail(),
                        validUpdateRequest.getAge(),
                        LocalDateTime.now())));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(validUpdateRequest.getName()))
                .andExpect(jsonPath("$.email").value(validUpdateRequest.getEmail()));
    }

    @Test
    void shouldReturn400WhenUpdateUserWithInvalidData() throws Exception {
        UserUpdateRequest invalidUpdate = new UserUpdateRequest("", "wrong-email", -1);


        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }
}