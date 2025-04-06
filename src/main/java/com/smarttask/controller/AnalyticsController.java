package com.smarttask.controller;

import com.smarttask.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {
    
    private final TaskRepository taskRepository;

    public AnalyticsController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/task-stats")
    public ResponseEntity<?> getTaskCompletionStats() {
        long completedTasks = taskRepository.countByStatus("COMPLETED");
        long pendingTasks = taskRepository.countByStatus("PENDING");

        Map<String, Long> stats = new HashMap<>();
        stats.put("Completed", completedTasks);
        stats.put("Pending", pendingTasks);

        return ResponseEntity.ok(stats);
    }
}
