package com.curso.tasks.controller;

import com.curso.tasks.model.ApiResponse;
import com.curso.tasks.model.Task;
import com.curso.tasks.model.TaskStatsResponse;
import com.curso.tasks.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> getAll(
            @RequestParam(required = false) Task.Status status) {
        List<Task> tasks = status != null
                ? taskService.findByStatus(status)
                : taskService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(tasks));
    }

    /**
     * Returns task counts grouped by status.
     *
     * @return task counts per status wrapped in {@link ApiResponse}
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TaskStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(taskService.getStats()));
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<Map<Task.Status, List<Task>>>> getGrouped() {
        return ResponseEntity.ok(ApiResponse.ok(taskService.groupByStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> getById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(task -> ResponseEntity.ok(ApiResponse.ok(task)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Task not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Task>> create(@RequestBody Task task) {
        try {
            Task created = taskService.create(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> update(
            @PathVariable Long id, @RequestBody Task task) {
        return taskService.update(id, task)
                .map(updated -> ResponseEntity.ok(ApiResponse.ok(updated)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Task not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (taskService.delete(id)) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Task not found"));
    }
}
