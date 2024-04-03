package main.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    protected TaskTypes type = TaskTypes.TASK;
    private UUID id;
    private String name;
    private String description;
    private TaskStatus taskStatus;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(
        UUID id,
        String name,
        String description,
        TaskStatus taskStatus,
        LocalDateTime startTime,
        Duration duration
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskTypes getType() {
        return type;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return getId() + "," + type + "," + getName() + "," + getTaskStatus() + "," + getDescription() + ","
                + getStartTime() + "," + getDuration().toMinutes();
    }
}