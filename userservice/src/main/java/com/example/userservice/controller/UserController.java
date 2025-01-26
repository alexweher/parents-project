package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Создание пользователя
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("Creating a new user: {}", user);

        // Кодирование пароля перед сохранением
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Создание пользователя
        User createdUser = userService.createUser(user);

        // Возврат успешного ответа с созданным пользователем
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Получение всех пользователей
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Получение пользователя по Id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        logger.info("Fetching user with ID: {}", id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    // Обновление пользователя
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id,
                                        @Valid @RequestBody User userDetails, BindingResult result) {
        if (result.hasErrors()) {
            logger.error("Validation errors while updating user with ID: {}", id);
            // Здесь исключение с деталями ошибок будет обработано глобально
            throw new IllegalArgumentException("Validation failed for user with ID " + id);
        }
        logger.info("Updating user with ID: {}", id);
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Удаление пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam(name = "email") String email) {
        logger.info("Received request to fetch user by email: {}", email);

        Optional<User> user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            logger.warn("No user found for email: {}", email);
            return ResponseEntity.notFound().build();
        }

        User userEntity = user.get();
        logger.info("User found: {}", userEntity);

        // Преобразование роли из строки в список
        List<String> roles = Arrays.asList(userEntity.getRoles().split(","));

        // Возвращаем UserDto с паролем
        UserDto userDto = new UserDto(
                userEntity.getEmail(),
                roles,
                userEntity.getPassword()  // передаем пароль
        );

        return ResponseEntity.ok(userDto);
    }

}
