package com.smarttask.service;

import com.smarttask.model.Notification;
import com.smarttask.model.User;
import com.smarttask.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository; // Assuming you have a repository for notifications

    // Get all notifications for a user
    public List<Notification> getNotificationsByUser(User user) {
        // Fetch notifications based on the user (this depends on your database design)
        return notificationRepository.findByUser(user); // Assuming you have a method like this in the repository
    }

 // Mark notification as read by ID
    public void markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(Notification.Status.READ);  // Set status to READ
            notificationRepository.save(notification);  // Save the updated notification
        } else {
            throw new RuntimeException("Notification not found");
        }
    }


    // Get notification by ID
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
}

