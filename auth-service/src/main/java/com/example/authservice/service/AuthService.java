package com.example.authservice.service;

import com.example.authservice.config.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserServiceClient userServiceClient;

    // Метод для аутентификации пользователя по email и паролю
    public boolean validateUserCredentials(String email, String password) {
        try {
            logger.info("Attempting authentication for user: {}", email);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authentication successful for user: {}", email);
            return true; // Если аутентификация прошла успешно
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", email);
            return false; // Если аутентификация не прошла
        }
    }

    // Метод для выполнения аутентификации и генерации токена
    public String authenticateAndGenerateToken(String username, String password) throws BadCredentialsException {
        try {
            // Получаем пользователя из базы
            logger.info("Fetching user details for email: {}", username);
            var userDto = userServiceClient.getUserByEmail(username);
            if (userDto == null) {
                logger.error("User not found with email: {}", username);
                throw new BadCredentialsException("Invalid username or password");
            }

            logger.info("Found user: {}", userDto);

            // Логируем полученный закодированный пароль
            logger.info("Encoded password from database: {}", userDto.getPassword());
            logger.info("Raw password from request: {}", password);

            // Проверка пароля
            if (!passwordEncoder.matches(password, userDto.getPassword())) {
                logger.error("Password mismatch for user: {}", username);
                throw new BadCredentialsException("Invalid username or password");
            }

            logger.info("Password validation successful for user: {}", username);

            // Если проверка прошла, генерируем токен
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authentication context set for user: {}", username);
            return jwtTokenUtil.generateToken(username); // Возвращаем токен
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for username: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
