package com.smarttask.service;

import com.smarttask.exception.TaskNotFoundException;
import com.smarttask.model.Task;
import com.smarttask.model.User;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
public class TaskService {

    private static final Logger LOGGER = Logger.getLogger(TaskService.class.getName());

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;  // Injecting EmailService

    // ✅ Create a new task (Ensures assigned user exists)
    public Task createTask(Task task) {
        if (task.getUser() == null || task.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID must be provided for task assignment.");
        }

        LOGGER.info("Received Task Assignment Request for User ID: " + task.getUser().getId());

        User assignedUser = userRepository.findById(task.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(assignedUser); // ✅ Assign to correct user
        Task savedTask = taskRepository.save(task);

        // Send email notification after task creation
        String subject = "New Task Assigned: " + savedTask.getTitle();
        String message = "You have been assigned a new task: " + savedTask.getTitle() +
                         "\nDescription: " + savedTask.getDescription() +
                         "\nDue Date: " + savedTask.getDueDate();
        emailService.sendEmail(savedTask.getUser().getEmail(), subject, message);

        LOGGER.info("Task Created and Assigned to User ID: " + savedTask.getUser().getId());

        return savedTask;
    }

    // ✅ Get task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
    }

    // ✅ Get all tasks assigned to a specific user
    public List<Task> getTasksByUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Valid User ID must be provided.");
        }
        return taskRepository.findByUser(user);
    }

    // ✅ Get tasks by status
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    // ✅ Get tasks by priority
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    // ✅ Update task (Ensures task exists before updating)
    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        // Preserve the assigned user to prevent unintentional changes
        if (updatedTask.getUser() == null) {
            updatedTask.setUser(existingTask.getUser());
        }

        updatedTask.setId(id);
        
        Task savedTask = taskRepository.save(updatedTask);

        // Send email notification after task update
        if (!existingTask.getStatus().equals(updatedTask.getStatus())) {
            String subject = "Task Status Updated: " + savedTask.getTitle();
            String message = "The status of your task has been updated to: " + savedTask.getStatus() +
                             "\nTask Details:\n" + savedTask.getDescription() +
                             "\nDue Date: " + savedTask.getDueDate();
            emailService.sendEmail(savedTask.getUser().getEmail(), subject, message);
        }

        return savedTask;
    }

    // ✅ Delete task (Ensures task exists before deleting)
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }

        Task taskToDelete = taskRepository.findById(id).get();

        // Send email notification when task is deleted
        String subject = "Task Deleted: " + taskToDelete.getTitle();
        String message = "Your task has been deleted: " + taskToDelete.getTitle() +
                         "\nTask Description: " + taskToDelete.getDescription();
        emailService.sendEmail(taskToDelete.getUser().getEmail(), subject, message);

        taskRepository.deleteById(id);
    }

    // ✅ Search and Filter Tasks by keyword, status, and priority
    public List<Task> searchAndFilterTasks(String keyword, Task.Status status, Task.Priority priority, User user) {
        if (keyword != null) {
            keyword = "%" + keyword.toLowerCase() + "%";
        }
        return taskRepository.searchAndFilterTasks(keyword, status, priority, user);
    }
}
