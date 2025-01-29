package com.example.userservice.config;

import com.example.userservice.service.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MyUserDetailsService userDetailsService;

    @Value("${jwt.secret-key}")
    private String secretKey; // Берем ключ из конфигурации

    public JwtAuthenticationFilter(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7); // Убираем "Bearer "
        String username = extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }

    private String extractUsername(String token) {
        return Jwts.parser()  // Используем parser
                .setSigningKey(secretKey)  // Используем ключ из конфигурации
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = Jwts.parser()  // Используем parser
                .setSigningKey(secretKey)  // Используем ключ из конфигурации
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject().equals(userDetails.getUsername()) && claims.getExpiration().after(new Date());
    }
}
