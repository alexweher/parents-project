package com.example.authservice.config;

import com.example.authservice.dto.UserDto;
import com.example.authservice.service.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    UserServiceClient userServiceClient;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http,
                                             AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())// Отключаем CSRF защиту, если используем токены
                .authorizeRequests()
                .requestMatchers("/auth/login", "/auth/validate").permitAll()
                .anyRequest().authenticated()
                .and()
                // Добавляем фильтр JWT
                 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserDto userDto = userServiceClient.getUserByEmail(username);
                if (userDto == null) {
                    throw new UsernameNotFoundException("User not found with email: " + username);
                }
                return User.withUsername(userDto.getEmail())
                        .password(userDto.getPassword())
                        .roles(userDto.getRoles().toArray(new String[0])) // если roles - это список строк
                        .build();
            }
        };
    }

}