package com.example.userservice.controller;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;





@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void testCreateUser() throws Exception {
        // JSON с данными пользователя, которого мы хотим создать
        String userJson = "{\"email\": \"test5@example.com\", \"name\": \"John Doe\", \"password\": \"password123\"}";

        //Выполнение Post запроса для создания пользователя и проверка ответа
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())  // Проверка, что статус ответа 201 (Created)
                .andExpect(jsonPath("$.email").value("test5@example.com"))  // Проверка содержимого ответа
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void testGetUserById() throws Exception {
        // Создаем нового пользователя
        String userJson = "{\"email\": \"search5@example.com\", \"name\": \"Jane Doe\", \"password\": \"password123\"}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Получаем ID созданного пользователя
        String response = result.getResponse().getContentAsString();
        Long userId = JsonPath.parse(response).read("$.id", Long.class);

        // Ищем пользователя по ID
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("search5@example.com"))
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Выполняем GET-запрос для получения всех пользователей
        // Сначала создаём пользователя
        String userJson = "{\"email\": \"unique@example.com\", \"name\": \"Jane Doe\", \"password\": \"password123\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        // Теперь тестируем получение всех пользователей
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())  // Проверяем, что запрос успешен
                .andExpect(jsonPath("$").isArray())  // Проверяем, что ответ — это массив
                .andExpect(jsonPath("$[0].email").value("unique@example.com"));  // Проверяем, что email первого пользователя совпадает
    }

    @Test
    public void testGetUserByEmail() throws Exception{
        // Создаем нового пользователя
        String userJson = "{\"email\": \"emailsearch@example.com\", \"name\": \"Jane Doe\", \"password\": \"password123\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        //Выполняем Get - запрос для поиска пользователя по email
        mockMvc.perform(get("/users/email/emailsearch@example.com"))
                .andExpect(status().isOk()) //проверяем что запрос успешен
                .andExpect(jsonPath("$.email")
                        .value("emailsearch@example.com"))//проверяем email
                .andExpect(jsonPath("$.name")
                        .value("Jane Doe"))//проверяем имя
                .andExpect(jsonPath("$.password")
                        .value(matchesPattern("^\\$2a\\$10\\$.+")));//проверяем пароль
    }

    @Test
    public void testUpdateUser() throws Exception {

        // Шифрование пароля для теста
        String rawPassword = "password321";

        // Создаем нового пользователя
        String userJson = "{\"email\": \"update@example.com\", \"name\": \"John Doe\",\"password\": \"password123\"}";

        //выполняем post запрос для создания нового пользователя
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();

        //получаем id созданного пользователя
        String response = result.getResponse()
                .getContentAsString();
        Long userId = JsonPath.parse(response)
                .read("$.id", Long.class);

        //JSON с новыми данными пользователя для обновления
        String updatedUserJson = "{\"email\": \"updated@example.com\", \"name\": \"John Smith\", \"password\": \"" + rawPassword + "\"}";

        //Выполняем Put запрос для обновления пользователя
        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())//проверяем что запрос успешен
                .andExpect(jsonPath("$.email").value("updated@example.com"))//проверяем что email обновлен
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.password").isNotEmpty()); // Убедитесь, что пароль зашифрован
    }

    @Test
    public void testDeleteUser() throws Exception {
        // Создаем нового пользователя
        String userJson = "{\"email\": \"delete@example.com\", \"name\": \"Delete Me\", \"password\": \"password123\"}";

        // Выполняем POST запрос для создания нового пользователя
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Получаем ID созданного пользователя
        String response = result.getResponse().getContentAsString();
        Long userId = JsonPath.parse(response).read("$.id", Long.class);

        // Выполняем DELETE запрос для удаления пользователя
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNoContent());  // Проверяем, что запрос вернул статус 204 (No Content)

        // Пытаемся найти удалённого пользователя (ожидаем, что его больше нет)
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());  // Проверяем, что пользователь не найден
    }

}