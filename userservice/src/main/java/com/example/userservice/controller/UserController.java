package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    // Создание пользователя
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Creating a new user: {}", userDto);
        // Создание пользователя через сервис
        UserDto createdUser = userService.createUser(userDto);
        // Возврат успешного ответа с созданным пользователем
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserDto> userDto = userService.getAllUsers();  // Получаем список UserDto от сервиса
        return ResponseEntity.ok(userDto);  // Возвращаем список UserDto
    }


    // Получение пользователя по Id
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        logger.info("Fetching user with ID: {}", id);
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // Обновление пользователя
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id,
                                        @Valid @RequestBody UserDto userDetails) {
        logger.info("Updating user with ID: {}", id);
        UserDto updatedUserDto = userService.updateUser(id, userDetails); // передаем UserDto в сервис
        return ResponseEntity.ok(updatedUserDto); // возвращаем UserDto
    }


    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam(name = "email") String email) {
        logger.info("Received request to fetch user by email: {}", email);

        Optional<UserDto> userDto = userService.getUserByEmail(email);

        if (userDto.isEmpty()) {
            logger.warn("No user found for email: {}", email);
            return ResponseEntity.notFound().build();
        }
        logger.info("Returning UserDto for email: {}", email);

        return ResponseEntity.ok(userDto.get());
    }


    // Удаление пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        logger.info("Received request to delete user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
