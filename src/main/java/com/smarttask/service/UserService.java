package com.smarttask.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.smarttask.model.User;
import com.smarttask.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user
    public User registerUser(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(User.Role.USER); // Assign default role USER
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully: {}", savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error registering user: {}", user.getUsername(), e);
            throw new RuntimeException("Error registering user", e);
        }
    }

    // Authenticate user (Login)
    public boolean authenticate(String username, String rawPassword) {
        logger.info("Authenticating user: {}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
            logger.info("Authentication successful for user: {}", username);
            return true;
        } else {
            logger.warn("Authentication failed for user: {}", username);
            return false;
        }
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    // Get user by username (throws exception if not found)
    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new RuntimeException("User not found: " + username);
                });
    }

    // Find user by username and return Optional
    public Optional<User> findByUsername(String username) {
        logger.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    // Get user by ID (returning Optional)
    public Optional<User> getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        return Optional.ofNullable(userRepository.findById(userId).orElse(null));
    }
}
