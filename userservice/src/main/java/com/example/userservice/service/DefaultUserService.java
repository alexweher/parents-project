package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultUserService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // Метод для проверки пустого пароля
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    private User convertDtoToUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRoles(String.join(",", userDto.getRoles()));  // Преобразуем список ролей в строку
        return user;
    }

    public UserDto convertUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(Arrays.asList(user.getRoles().split(",")));  // Преобразуем строку ролей обратно в список
        return userDto;
    }

    public UserDto createUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto cannot be null");
        }

        logger.info("Attempting to create a new user: {}", userDto.getEmail());

        validatePassword(userDto.getPassword());

        // Проверяем, существует ли уже пользователь с таким email
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Преобразуем DTO в User
        User user = convertDtoToUser(userDto);

        // Кодируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Сохраняем пользователя в БД
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return convertUserToDto(savedUser);
    }




    public List<UserDto> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Total users found: {}", users.size());

        // Преобразуем List<User> в List<UserDto>
        return users.stream()
                .map(this::convertUserToDto)  // Преобразуем каждый User в UserDto
                .collect(Collectors.toList());
    }


    // Метод получения пользователя по ID и преобразования в UserDto
    public UserDto getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User with ID " + id + " not found");
                });
        logger.info("User found with ID: {}", id);
        return convertUserToDto(user); // Преобразуем в UserDto
    }


    // Метод для поиска пользователя по email
    public Optional<UserDto> getUserByEmail(String email) {
        logger.info("Fetching user with email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            logger.warn("User not found with email: {}", email);
            return Optional.empty();
        }

        logger.info("User found with email: {}", email);
        // Преобразуем найденного пользователя в UserDto
        UserDto userDto = convertUserToDto(user.get());
        return Optional.of(userDto);
    }


    // Метод обновления пользователя
    public UserDto updateUser(Long id, UserDto userDetails) {
        logger.info("Updating user with ID: {}", id);

        // Получаем существующего пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        // Преобразуем данные из UserDto в User
        User userToUpdate = convertDtoToUser(userDetails);

        // Обновляем поля пользователя
        existingUser.setName(userToUpdate.getName());
        existingUser.setEmail(userToUpdate.getEmail());
        existingUser.setRoles(userToUpdate.getRoles());

        // Сохраняем обновленного пользователя
        User updatedUser = userRepository.save(existingUser);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());

        // Конвертируем обновленного пользователя в UserDto
        return convertUserToDto(updatedUser); // возвращаем UserDto
    }


    // Удаление пользователя
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        userRepository.delete(existingUser);
        logger.info("User deleted successfully with ID: {}", id);
    }
}
