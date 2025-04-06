package com.smarttask.controller;

import com.smarttask.model.Notification;
import com.smarttask.model.User;
import com.smarttask.service.NotificationService;
import com.smarttask.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // Get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        // Fetch user from the database
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get notifications for the user
        List<Notification> notifications = notificationService.getNotificationsByUser(user);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // Mark a notification as read
    @PutMapping("/mark-read/{id}")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        
        // Optionally fetch and return the updated notification
        Notification notification = notificationService.getNotificationById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }
}

