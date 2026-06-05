package com.curso.tasks.model;

/**
 * DTO that holds the count of tasks for each possible status.
 */
public class TaskStatsResponse {

    private long pending;
    private long inProgress;
    private long done;

    /**
     * Creates a stats snapshot with the given task counts.
     *
     * @param pending    number of tasks in PENDING status
     * @param inProgress number of tasks in IN_PROGRESS status
     * @param done       number of tasks in DONE status
     */
    public TaskStatsResponse(long pending, long inProgress, long done) {
        this.pending = pending;
        this.inProgress = inProgress;
        this.done = done;
    }

    public long getPending() { return pending; }
    public long getInProgress() { return inProgress; }
    public long getDone() { return done; }
}
