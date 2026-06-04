# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application (available at http://localhost:8080/api/tasks)
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=TaskServiceTest
mvn test -Dtest=TaskControllerIntegrationTest

# Build without running tests
mvn package -DskipTests
```

## Architecture

Standard Spring Boot layered architecture: `TaskController` → `TaskService` → `TaskRepository`.

- **`Task`** — JPA entity with `PENDING / IN_PROGRESS / DONE` status enum. `createdAt` is set at construction and is not updatable. `title` is required; `description` is optional.
- **`TaskRepository`** — extends `JpaRepository<Task, Long>`. The only custom query method is `findByStatus`.
- **`TaskService`** — owns all business logic, including the `title` blank-check on create and the partial-update logic in `update()` (only non-null/non-blank fields overwrite the existing entity).
- **`ApiResponse<T>`** — generic response wrapper with `success`, `data`, and `error` fields. Every endpoint returns this type; the model is never returned directly.
- **H2 in-memory DB** — schema is created and dropped on each run (`ddl-auto=create-drop`). H2 console at `/h2-console`.

## Tests

Two test classes:

- `TaskServiceTest` — unit tests using Mockito; tests the service layer in isolation.
- `TaskControllerIntegrationTest` — `@SpringBootTest` + `@AutoConfigureMockMvc`; tests the full HTTP stack against the real H2 database.

## Project rules

- Controllers never call repository methods directly. All logic passes through the service.
- All endpoints return `ApiResponse<T>`. Never return the model directly from a controller method.
