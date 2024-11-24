package com.example.userservice.service;


import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    //Метод для создания пользователя
    public User createUser(User user){
        logger.info("Attempting to create a new user: {}", user);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;

    }


    //Метод ля получения всех пользователей
    public List<User> getAllUsers(){
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Total users found: {}", users.size());
        return users;
    }


    //Метод получения пользователя по ID
    public Optional<User> getUserById(Long id){
        logger.info("Fetching user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User found with ID: {}", id);
        } else {
            logger.warn("User not found with ID: {}", id);
        }
        return user;
    }


    //Метод для поиска пользователя по email
    public Optional<User> getUserByEmail(String email){
        logger.info("Fetching user with email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            logger.info("User found with email: {}", email);
        } else {
            logger.warn("User not found with email: {}", email);
        }
        return user;
    }


    // Метод обновления пользователя
    public User updateUser(Long id, User userDetails) {
        logger.info("Updating user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(()->{
                    logger.error("User not found with ID: {}", id);
                   return new  ResourceNotFoundException("User not found with id: " + id);
                });

        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());

        User updatedUser = userRepository.save(existingUser);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return updatedUser;

    }

    // удаление пользователя
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
