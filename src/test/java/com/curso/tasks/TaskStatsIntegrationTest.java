package com.curso.tasks;

import com.curso.tasks.model.Task;
import com.curso.tasks.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void clearDatabase() {
        taskRepository.deleteAll();
    }

    @Test
    void getStats_emptyDatabase_returnsZeros() throws Exception {
        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pending").value(0))
                .andExpect(jsonPath("$.data.inProgress").value(0))
                .andExpect(jsonPath("$.data.done").value(0));
    }

    @Test
    void getStats_returnsCountPerStatus() throws Exception {
        // Create 2 PENDING tasks
        createTask("Task A", Task.Status.PENDING);
        createTask("Task B", Task.Status.PENDING);

        // Create 1 IN_PROGRESS task
        createTask("Task C", Task.Status.IN_PROGRESS);

        // Create 3 DONE tasks
        createTask("Task D", Task.Status.DONE);
        createTask("Task E", Task.Status.DONE);
        createTask("Task F", Task.Status.DONE);

        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pending").value(2))
                .andExpect(jsonPath("$.data.inProgress").value(1))
                .andExpect(jsonPath("$.data.done").value(3));
    }

    // Helper: creates a task with the given title and status via the API
    private void createTask(String title, Task.Status status) throws Exception {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());
    }
}
