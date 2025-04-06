package com.smarttask.service;

import com.smarttask.model.User;
import com.smarttask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        adminUser = new User(1L, "admin", "password", "admin@email.com", User.Role.ADMIN);
        normalUser = new User(2L, "user", "password", "user@email.com", User.Role.USER);
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // No Optional, directly returning User
        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("admin", foundUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(3L));

        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    void testGetUserByUsername_UserExists() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        User foundUser = userService.getUserByUsername("admin");

        assertNotNull(foundUser);
        assertEquals("admin@email.com", foundUser.getEmail());
        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserByUsername("unknown"));

        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
