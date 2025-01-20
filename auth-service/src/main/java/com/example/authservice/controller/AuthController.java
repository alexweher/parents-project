package com.example.authservice.controller;

import com.example.authservice.dto.JwtResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.ValidationResponse;
import com.example.authservice.dto.CredentialValidationRequest;
import com.example.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Получен запрос на вход для пользователя: {}", loginRequest.getUsername());
        try {
            // Аутентификация пользователя и генерация токена
            String token = authService.authenticateAndGenerateToken(loginRequest.getUsername(), loginRequest.getPassword());
            logger.info("Токен успешно сгенерирован для пользователя: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            logger.warn("Ошибка аутентификации для пользователя: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateUserCredentials(@Valid @RequestBody CredentialValidationRequest request) {
        logger.info("Валидация учетных данных для email: {}", request.getEmail());
        boolean isValid = authService.validateUserCredentials(request.getEmail(), request.getPassword());
        logger.info("Результат валидации для {}: {}", request.getEmail(), isValid);
        return ResponseEntity.ok(new ValidationResponse(isValid));
    }
}
