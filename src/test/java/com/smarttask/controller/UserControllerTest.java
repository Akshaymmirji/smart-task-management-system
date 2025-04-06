package com.smarttask.controller;

import com.smarttask.model.User;
import com.smarttask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "user1", "password", "user1@email.com", User.Role.USER);
    }

    @Test
    void testGetUserById_UserExists() {
        // Mock the service method to return the user directly (no Optional)
        when(userService.getUserById(1L)).thenReturn(testUser);

        // Test the controller method
        ResponseEntity<User> response = userController.getUserById(1L);

        // Verify the response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user1", response.getBody().getUsername());
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Mock the service method to return null if the user is not found
        when(userService.getUserById(2L)).thenReturn(null);

        // Test the controller method
        ResponseEntity<User> response = userController.getUserById(2L);

        // Verify the response
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
