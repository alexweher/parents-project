package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.model.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto updateUser(Long id, @Valid UserDto userDetails);

    void deleteUser(Long id);

    Optional<UserDto> getUserByEmail(String email);
}
