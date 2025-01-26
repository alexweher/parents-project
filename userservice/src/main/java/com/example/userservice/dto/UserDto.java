package com.example.userservice.dto;

import java.util.List;

public class UserDto {
    private String email;
    private List<String> roles;
    private String password; // Добавляем поле для пароля

    public UserDto(String email, List<String> roles, String password) {
        this.email = email;
        this.roles = roles;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
