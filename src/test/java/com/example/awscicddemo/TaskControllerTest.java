package com.example.awscicddemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskControllerTest {

    private TaskController controller;

    @BeforeEach
    void setUp() {
        controller = new TaskController();
    }

    @Test
    @DisplayName("Should create a task when valid request is provided")
    void testAddTaskSuccess() {
        var request = new TaskController.CreateTaskRequest("Complete CIA 2 CI/CD Pipeline");
        ResponseEntity<TaskController.TaskItem> response = controller.addTask(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Complete CIA 2 CI/CD Pipeline", response.getBody().title());
        assertFalse(response.getBody().completed());
    }

    @Test
    @DisplayName("Should reject blank task title")
    void testAddTaskBlankTitle() {
        var request = new TaskController.CreateTaskRequest("   ");
        ResponseEntity<TaskController.TaskItem> response = controller.addTask(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should list all created tasks")
    void testGetTasks() {
        controller.addTask(new TaskController.CreateTaskRequest("Task 1"));
        controller.addTask(new TaskController.CreateTaskRequest("Task 2"));

        List<TaskController.TaskItem> tasks = controller.getTasks();
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).title());
        assertEquals("Task 2", tasks.get(1).title());
    }

    @Test
    @DisplayName("Should toggle task completion status")
    void testToggleTask() {
        ResponseEntity<TaskController.TaskItem> created = controller.addTask(new TaskController.CreateTaskRequest("Toggle Me"));
        assertNotNull(created.getBody());
        long taskId = created.getBody().id();

        ResponseEntity<TaskController.TaskItem> toggled = controller.toggleTask(taskId);
        assertEquals(HttpStatus.OK, toggled.getStatusCode());
        assertNotNull(toggled.getBody());
        assertTrue(toggled.getBody().completed());

        // Toggle back
        ResponseEntity<TaskController.TaskItem> toggledBack = controller.toggleTask(taskId);
        assertNotNull(toggledBack.getBody());
        assertFalse(toggledBack.getBody().completed());
    }

    @Test
    @DisplayName("Should return 404 when toggling non-existent task")
    void testToggleNonExistentTask() {
        ResponseEntity<TaskController.TaskItem> response = controller.toggleTask(9999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTask() {
        ResponseEntity<TaskController.TaskItem> created = controller.addTask(new TaskController.CreateTaskRequest("Delete Me"));
        assertNotNull(created.getBody());
        long taskId = created.getBody().id();

        ResponseEntity<Void> deleteResponse = controller.deleteTask(taskId);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify it is gone
        ResponseEntity<TaskController.TaskItem> toggleResponse = controller.toggleTask(taskId);
        assertEquals(HttpStatus.NOT_FOUND, toggleResponse.getStatusCode());
    }
}
