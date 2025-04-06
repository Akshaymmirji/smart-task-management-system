package com.smarttask.repository;

import com.smarttask.model.Task;
import com.smarttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);  // ✅ Corrected query
    List<Task> findByStatus(Task.Status status);
    List<Task> findByPriority(Task.Priority priority);
    long countByStatus(String status);
    
    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN CURRENT_TIMESTAMP AND :tomorrow")
    List<Task> findTasksWithUpcomingDeadlines(@Param("tomorrow") LocalDateTime tomorrow);
    
    @Query("SELECT t FROM Task t WHERE " +
           "(:user IS NULL OR t.user = :user) AND " +
           "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    List<Task> searchAndFilterTasks(
            @Param("keyword") String keyword,
            @Param("status") Task.Status status,
            @Param("priority") Task.Priority priority,
            @Param("user") User user);
}
