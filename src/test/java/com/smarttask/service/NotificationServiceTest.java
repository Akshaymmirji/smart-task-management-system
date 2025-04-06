package com.smarttask.service;

import com.smarttask.model.Notification;
import com.smarttask.model.User;
import com.smarttask.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1L, "user1", "password", "user1@email.com", User.Role.USER);
        testNotification = new Notification("Task Created", Notification.Status.UNREAD, testUser, LocalDateTime.now());
    }

    @Test
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        Notification savedNotification = notificationService.createNotification("Task Created", testUser);
        
        assertNotNull(savedNotification);
        assertEquals("Task Created", savedNotification.getMessage());
        assertEquals(Notification.Status.UNREAD, savedNotification.getStatus());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications() {
        when(notificationRepository.findByUser(testUser)).thenReturn(Arrays.asList(testNotification));

        List<Notification> notifications = notificationService.getUserNotifications(testUser);

        assertEquals(1, notifications.size());
        assertEquals("Task Created", notifications.get(0).getMessage());
    }

    @Test
    void testMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification updatedNotification = notificationService.markAsRead(1L);

        assertNotNull(updatedNotification);
        assertEquals(Notification.Status.READ, updatedNotification.getStatus());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
