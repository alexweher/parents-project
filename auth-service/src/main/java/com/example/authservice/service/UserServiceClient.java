package com.example.authservice.service;

import com.example.authservice.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    @Value("${userservice.url}")
    private String userServiceUrl;

    private final WebClient webClient;

    @Autowired
    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public UserDto getUserByEmail(String email) {
        logger.debug("Requesting user with email: {}", email);

        try {
            // Логируем URL запроса
            String uri = userServiceUrl + "/users/by-email?email={email}";
            logger.debug("Sending GET request to URL: {}", uri);

            UserDto userDto = webClient.get()
                    .uri(uri, email)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block(); // Синхронный вызов

            // Логируем результат
            if (userDto != null) {
                logger.info("Successfully fetched user with email: {}", email);
            } else {
                logger.warn("No user found for email: {}", email);
            }

            return userDto;
        } catch (WebClientResponseException.NotFound e) {
            logger.error("User not found with email: {}", email, e);
            return null; // Если пользователь не найден
        } catch (Exception e) {
            logger.error("Error occurred while calling UserService for email: {}", email, e);
            throw new RuntimeException("Ошибка при вызове userservice", e);
        }
    }
}
