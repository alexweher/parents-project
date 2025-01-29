package com.example.userservice.config;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;


    @Value("${jwt.expiration-time}")
    private long expirationTime;

    // Метод для извлечения имени пользователя из токена
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Метод для проверки токена на валидность
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Метод для проверки, истек ли срок действия токена
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Метод для извлечения даты истечения токена
    private Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
