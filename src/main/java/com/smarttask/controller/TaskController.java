package com.smarttask.controller;

import com.smarttask.model.Task;
import com.smarttask.model.User;
import com.smarttask.service.TaskService;
import com.smarttask.service.UserService;
import com.smarttask.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService; // Injecting the EmailService

    // Helper method to get the authenticated user
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return userOpt.get();
    }

    // Helper method to parse the deadline
    private LocalDateTime parseDeadline(String deadlineStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(deadlineStr, formatter);
    }

    // Create a new task
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        User user = getAuthenticatedUser(); // Get the authenticated user

        // Parse the deadline before saving
        if (task.getDeadline() != null) {
            task.setDeadline(parseDeadline(task.getDeadline().toString()));
        }

        // Admin can assign tasks to other users, regular users can only create their own tasks
        if (user.getRole().equals(User.Role.ADMIN)) {
            if (task.getUser() == null || task.getUser().getId() == null) {
                return ResponseEntity.badRequest().build();
            }
            Optional<User> assignedUserOpt = userService.getUserById(task.getUser().getId());
            if (assignedUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            task.setUser(assignedUserOpt.get());
        } else {
            if (task.getUser() != null && !task.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            task.setUser(user); // Set the task's user to the authenticated user
        }

        Task createdTask = taskService.createTask(task);

        // Send a notification email after creating the task
        String subject = "Task Created Successfully";
        String text = "Hello " + user.getUsername() + ",\n\nYour task has been created successfully:\n\n" +
                      "Task ID: " + createdTask.getId() + "\n" +
                      "Task Title: " + createdTask.getTitle() + "\n\n" +
                      "Thank you!";
        emailService.sendEmail(user.getEmail(), subject, text); // Send email to the user

        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        User user = getAuthenticatedUser(); // Get the authenticated user

        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (!user.getRole().equals(User.Role.ADMIN) && !task.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(task);
    }

    // Update task
    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        User user = getAuthenticatedUser(); // Get the authenticated user

        Task existingTask = taskService.getTaskById(id);
        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (!user.getRole().equals(User.Role.ADMIN) && !existingTask.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Parse the deadline before saving
        if (task.getDeadline() != null) {
            task.setDeadline(parseDeadline(task.getDeadline().toString()));
        }

        task.setId(id);
        task.setUser(existingTask.getUser()); // Keep the user the same for updates
        Task updatedTask = taskService.updateTask(id, task);

        // Send a notification email after updating the task
        String subject = "Task Updated Successfully";
        String text = "Hello " + user.getUsername() + ",\n\nYour task has been updated successfully:\n\n" +
                      "Task ID: " + updatedTask.getId() + "\n" +
                      "Task Title: " + updatedTask.getTitle() + "\n\n" +
                      "Thank you!";
        emailService.sendEmail(user.getEmail(), subject, text); // Send email to the user

        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    // Delete task
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        User user = getAuthenticatedUser(); // Get the authenticated user

        Task existingTask = taskService.getTaskById(id);
        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (!user.getRole().equals(User.Role.ADMIN) && !existingTask.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        taskService.deleteTask(id);

        // Send a notification email after deleting the task
        String subject = "Task Deleted Successfully";
        String text = "Hello " + user.getUsername() + ",\n\nYour task has been deleted successfully:\n\n" +
                      "Task ID: " + existingTask.getId() + "\n" +
                      "Task Title: " + existingTask.getTitle() + "\n\n" +
                      "Thank you!";
        emailService.sendEmail(user.getEmail(), subject, text); // Send email to the user

        return ResponseEntity.ok("Task deleted successfully.");
    }

    // Get tasks by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable Long userId) {
        User loggedInUser = getAuthenticatedUser(); // Get the authenticated user

        Optional<User> targetUserOpt = userService.getUserById(userId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User targetUser = targetUserOpt.get();

        if (!loggedInUser.getRole().equals(User.Role.ADMIN) && !loggedInUser.equals(targetUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Task> tasks = taskService.getTasksByUser(targetUser);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Search tasks with filtering options
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {

        User user = getAuthenticatedUser(); // Get the authenticated user

        Task.Status taskStatus = null;
        Task.Priority taskPriority = null;

        try {
            if (status != null) {
                taskStatus = Task.Status.valueOf(status.toUpperCase());
            }
            if (priority != null) {
                taskPriority = Task.Priority.valueOf(priority.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<User> filterUserOpt = userService.getUserById(userId);
        List<Task> tasks = taskService.searchAndFilterTasks(keyword, taskStatus, taskPriority, filterUserOpt.orElse(null));
        return ResponseEntity.ok(tasks);
    }
}
