package com.example.authservice.service;

import com.example.userservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String userServiceUrl = "http://localhost:8080/users/";

    public User getUserByUsername(String username) {
        String url = userServiceUrl + "email/" + username;
        return restTemplate.getForObject(url, User.class);  // Отправка GET-запроса
    }
}
