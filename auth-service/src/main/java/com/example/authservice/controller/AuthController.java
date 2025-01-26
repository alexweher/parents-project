package com.example.authservice.controller;

import com.example.authservice.dto.JwtResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.ValidationResponse;
import com.example.authservice.dto.CredentialValidationRequest;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.UserServiceClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserServiceClient userServiceClient;
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, UserServiceClient userServiceClient,PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Получен запрос на вход для пользователя: {}", loginRequest.getUsername());
        try {
            // Пытаемся получить пользователя из userservice по email
            var userDto = userServiceClient.getUserByEmail(loginRequest.getUsername());

            if (userDto == null) {
                logger.warn("Пользователь с таким email не найден: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            // Проверяем, совпадает ли введенный пароль с тем, что хранится в базе
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), userDto.getPassword());
            logger.info("Пароль совпадает для пользователя {}: {}", loginRequest.getUsername(), passwordMatches);

            if (!passwordMatches) {
                logger.warn("Неверный пароль для пользователя: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            // Генерация JWT токена
            String token = authService.authenticateAndGenerateToken(loginRequest.getUsername(),
                    loginRequest.getPassword());
            logger.info("Токен успешно сгенерирован для пользователя: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (Exception e) {
            logger.error("Ошибка при аутентификации пользователя: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }


    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateUserCredentials(
            @Valid @RequestBody CredentialValidationRequest request) {
        logger.info("Валидация учетных данных для email: {}", request.getEmail());

        // Получаем данные о пользователе
        var userDto = userServiceClient.getUserByEmail(request.getEmail());

        // Если пользователя не нашли, то считаем, что его учетные данные некорректны
        boolean isValid = userDto != null && passwordEncoder.matches(request.getPassword(), userDto.getPassword());


        logger.info("Результат валидации для {}: {}", request.getEmail(), isValid);
        return ResponseEntity.ok(new ValidationResponse(isValid));
    }
}
