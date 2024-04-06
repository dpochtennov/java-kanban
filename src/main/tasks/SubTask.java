package main.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class SubTask extends Task {
    private final UUID epicID;

    public SubTask(UUID id, String name, String description, TaskStatus taskStatus, UUID epicID, LocalDateTime startTime, Duration duration) {
        super(id, name, description, taskStatus, startTime, duration);
        this.type = TaskTypes.SUBTASK;
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus taskStatus, UUID epicID, LocalDateTime startTime, Duration duration) {
        super(name, description, taskStatus, startTime, duration);
        this.type = TaskTypes.SUBTASK;
        this.epicID = epicID;
    }

    public UUID getEpicId() {
        return epicID;
    }

    @Override
    public String toString() {
        return super.toString() + "," + getEpicId();
    }
}