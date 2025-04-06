package com.smarttask.controller;

import com.smarttask.model.Notification;
import com.smarttask.model.User;
import com.smarttask.service.NotificationService;
import com.smarttask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1L, "user1", "password", "user1@example.com", User.Role.USER);
        testNotification = new Notification("Task Due Soon", Notification.Status.UNREAD, testUser, null);
    }

    // ✅ Test: Get Notifications for User
    @Test
    void testGetUserNotifications() {
        List<Notification> notifications = Arrays.asList(testNotification);

        // If getUserById returns User directly
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(notificationService.getUserNotifications(testUser)).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getUserNotifications(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Task Due Soon", response.getBody().get(0).getMessage());
    }

    // ✅ Test: Mark Notification as Read
    @Test
    void testMarkNotificationAsRead() {
        testNotification.setStatus(Notification.Status.READ);
        when(notificationService.markAsRead(1L)).thenReturn(testNotification);

        ResponseEntity<Notification> response = notificationController.markNotificationAsRead(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Notification.Status.READ, response.getBody().getStatus());
    }
}
