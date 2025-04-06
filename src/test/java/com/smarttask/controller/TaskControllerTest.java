package com.smarttask.controller;

import com.smarttask.model.Task;
import com.smarttask.model.User;
import com.smarttask.service.TaskService;
import com.smarttask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskController taskController;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1L, "user1", "password", "user1@example.com", User.Role.USER);
        testTask = new Task(1L, "Test Task", "Description", Task.Status.PENDING, testUser);
    }

    // ✅ Test: Get Tasks for User
    @Test
    void testGetUserTasks() {
        List<Task> tasks = Arrays.asList(testTask);

        // Mock userService.getUserById to return an Optional<User>
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // Mock taskService.getUserTasks to return the list of tasks
        when(taskService.getUserTasks(testUser)).thenReturn(tasks);

        // Call the controller method
        ResponseEntity<List<Task>> response = taskController.getUserTasks(1L);

        // Assert the results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getTitle());
    }

    // ✅ Test: Mark Task as Completed
    @Test
    void testMarkTaskAsCompleted() {
        // Update the status of the task to COMPLETED
        testTask.setStatus(Task.Status.COMPLETED);

        // Mock taskService.markAsCompleted to return the updated task
        when(taskService.markAsCompleted(1L)).thenReturn(testTask);

        // Call the controller method
        ResponseEntity<Task> response = taskController.markTaskAsCompleted(1L);

        // Assert the results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Task.Status.COMPLETED, response.getBody().getStatus());
    }
}
