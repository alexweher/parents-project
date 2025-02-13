package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
    @JsonIgnore
    private String password;

    public UserDto() {}

    public UserDto(Long id, String name, String email, List<String> roles, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
