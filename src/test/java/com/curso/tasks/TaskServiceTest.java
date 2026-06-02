package com.curso.tasks;

import com.curso.tasks.model.Task;
import com.curso.tasks.repository.TaskRepository;
import com.curso.tasks.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(repository);
    }

    @Test
    void create_withValidTitle_savesTask() {
        Task task = new Task();
        task.setTitle("Fix login bug");
        when(repository.save(any())).thenReturn(task);

        Task result = service.create(task);

        assertThat(result.getTitle()).isEqualTo("Fix login bug");
        verify(repository).save(task);
    }

    @Test
    void create_withBlankTitle_throwsException() {
        Task task = new Task();
        task.setTitle("   ");

        assertThatThrownBy(() -> service.create(task))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title is required");

        verify(repository, never()).save(any());
    }

    @Test
    void delete_existingId_returnsTrue() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = service.delete(1L);

        assertThat(result).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_returnsFalse() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean result = service.delete(99L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }

    @Test
    void findByStatus_delegatesToRepository() {
        Task task = new Task();
        task.setStatus(Task.Status.DONE);
        when(repository.findByStatus(Task.Status.DONE)).thenReturn(List.of(task));

        List<Task> result = service.findByStatus(Task.Status.DONE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Task.Status.DONE);
    }

    @Test
    void update_existingTask_updatesFields() {
        Task existing = new Task();
        existing.setTitle("Old title");
        existing.setStatus(Task.Status.PENDING);

        Task incoming = new Task();
        incoming.setTitle("New title");
        incoming.setStatus(Task.Status.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Task> result = service.update(1L, incoming);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("New title");
        assertThat(result.get().getStatus()).isEqualTo(Task.Status.IN_PROGRESS);
    }
}
