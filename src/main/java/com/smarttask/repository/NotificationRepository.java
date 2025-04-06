package com.smarttask.repository;

import com.smarttask.model.Notification;
import com.smarttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications for a specific user
    List<Notification> findByUser(User user);

    // Find unread notifications for a user
    List<Notification> findByUserAndStatus(User user, Notification.Status status);
    
    
}
