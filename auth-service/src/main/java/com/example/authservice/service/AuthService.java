package com.example.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    // Метод для аутентификации пользователя по email и паролю
    public boolean validateUserCredentials(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true; // Если аутентификация прошла успешно
        } catch (BadCredentialsException e) {
            return false; // Если аутентификация не прошла
        }
    }

    // Метод для выполнения аутентификации и генерации токена
    public String authenticateAndGenerateToken(String username, String password) throws BadCredentialsException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // После успешной аутентификации, генерируем JWT
            // Здесь вы добавляете код для генерации JWT токена (например, через JwtTokenUtil)
            return "generated_token"; // Возвращаем токен
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}