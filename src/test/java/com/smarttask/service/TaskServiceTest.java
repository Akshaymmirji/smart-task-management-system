package com.smarttask.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smarttask.model.Task;
import com.smarttask.model.User;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private User sampleUser;

    @BeforeEach
    void setup() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");

        sampleTask = new Task("New Task", "Test Desc", Task.Priority.HIGH, Task.Status.PENDING, LocalDateTime.now(), sampleUser);
    }

    @Test
    public void testCreateTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task createdTask = taskService.createTask(sampleTask);

        assertEquals("New Task", createdTask.getTitle());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    public void testGetTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Task fetchedTask = taskService.getTaskById(1L);

        assertEquals(sampleTask.getTitle(), fetchedTask.getTitle());
    }

    @Test
    public void testGetTaskById_NotFound() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            taskService.getTaskById(2L);
        });

        assertEquals("Task not found with ID: 2", exception.getMessage());
    }
}
