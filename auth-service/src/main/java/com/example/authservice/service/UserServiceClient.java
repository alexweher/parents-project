package com.example.authservice.service;


import com.example.authservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class UserServiceClient  {

    @Value("${userservice.url}")
    private String userServiceUrl;

    private final WebClient webClient;

    @Autowired
    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public UserDto getUserByEmail(String email) {
        try {
            return webClient.get()
                    .uri(userServiceUrl + "/users/by-email?email={email}", email)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block(); // Синхронный вызов
        } catch (WebClientResponseException.NotFound e) {
            return null; // Если пользователь не найден
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при вызове userservice", e);
        }
    }

}


