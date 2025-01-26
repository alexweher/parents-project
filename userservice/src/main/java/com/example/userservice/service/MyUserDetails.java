package com.example.userservice.service;

import com.example.userservice.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {

    private final User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Разделяем роли и создаем SimpleGrantedAuthority для каждой
        return Arrays.stream(user.getRoles().split(","))
                .map(role -> "ROLE_" + role.trim()) // Добавляем "ROLE_" к каждой роли, если это необходимо
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Используем email для аутентификации
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Логика зависит от твоих требований
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Логика зависит от твоих требований
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Логика зависит от твоих требований
    }

    @Override
    public boolean isEnabled() {
        return true;  // Логика зависит от твоих требований
    }
}
