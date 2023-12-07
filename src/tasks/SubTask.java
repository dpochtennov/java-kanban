package tasks;

import java.util.UUID;

public class SubTask extends Task {
    private final UUID epicID;

    public SubTask(UUID id, String name, String description, TaskStatus taskStatus, UUID epicID) {
        super(id, name, description, taskStatus);
        this.type = TaskTypes.SUBTASK;
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus taskStatus, UUID epicID) {
        super(name, description, taskStatus);
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