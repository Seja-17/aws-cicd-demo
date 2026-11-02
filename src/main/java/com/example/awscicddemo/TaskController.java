package com.example.awscicddemo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final Map<Long, TaskItem> tasks = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public List<TaskItem> getTasks() {
        return tasks.values().stream()
                .sorted(Comparator.comparingLong(TaskItem::id))
                .toList();
    }

    @PostMapping
    public ResponseEntity<TaskItem> addTask(@RequestBody CreateTaskRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        TaskItem task = new TaskItem(nextId.getAndIncrement(), request.title().trim(), false);
        tasks.put(task.id(), task);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskItem> toggleTask(@PathVariable long id) {
        TaskItem updated = tasks.computeIfPresent(id,
                (taskId, current) -> new TaskItem(taskId, current.title(), !current.completed()));
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        return tasks.remove(id) == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.noContent().build();
    }

    public record TaskItem(long id, String title, boolean completed) { }

    public record CreateTaskRequest(String title) { }
}
