package com.curso.tasks.service;

import com.curso.tasks.model.Task;
import com.curso.tasks.model.TaskStatsResponse;
import com.curso.tasks.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public List<Task> findByStatus(Task.Status status) {
        return repository.findByStatus(status);
    }

    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    public Task create(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        return repository.save(task);
    }

    public Optional<Task> update(Long id, Task incoming) {
        return repository.findById(id).map(existing -> {
            if (incoming.getTitle() != null && !incoming.getTitle().isBlank()) {
                existing.setTitle(incoming.getTitle());
            }
            if (incoming.getDescription() != null) {
                existing.setDescription(incoming.getDescription());
            }
            if (incoming.getStatus() != null) {
                existing.setStatus(incoming.getStatus());
            }
            return repository.save(existing);
        });
    }

    public Map<Task.Status, List<Task>> groupByStatus() {
        return Arrays.stream(Task.Status.values())
                .collect(Collectors.toMap(s -> s, this::findByStatus));
    }

    /**
     * Returns the count of tasks for each status.
     *
     * @return a {@link TaskStatsResponse} containing counts for PENDING, IN_PROGRESS, and DONE
     */
    public TaskStatsResponse getStats() {
        long pending = repository.countByStatus(Task.Status.PENDING);
        long inProgress = repository.countByStatus(Task.Status.IN_PROGRESS);
        long done = repository.countByStatus(Task.Status.DONE);
        return new TaskStatsResponse(pending, inProgress, done);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
