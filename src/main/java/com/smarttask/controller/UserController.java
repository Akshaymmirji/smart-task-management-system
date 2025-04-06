package com.smarttask.controller;

import com.smarttask.model.User;
import com.smarttask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        logger.info("Attempting to register user with email: {}", user.getEmail());
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email already exists - {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Email already exists
        }
        try {
            User newUser = userService.registerUser(user);
            logger.info("User registered successfully: {}", newUser.getUsername());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error during registration for user with email: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> {
                       logger.warn("User not found with ID: {}", id);
                       return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                   });
    }

    // ✅ Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> {
                       logger.warn("User not found with email: {}", email);
                       return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                   });
    }

    // ✅ Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.info("Fetching user with username: {}", username);
        try {
            User user = userService.getUserByUsername(username);
            logger.info("User found with username: {}", username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            logger.warn("User not found with username: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ✅ Get all users
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
