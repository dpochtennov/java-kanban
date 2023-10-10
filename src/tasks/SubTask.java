package tasks;

import java.util.UUID;

public class SubTask extends Task {
    private final UUID epicID;

    public SubTask(UUID id, String name, String description, TaskStatus taskStatus, UUID epicID) {
        super(id, name, description, taskStatus);
        this.epicID = epicID;
    }

    public UUID getEpicId() {
        return epicID;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + super.getId() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", epicID='" + epicID + '\'' +
                ", taskStatus=" + super.getTaskStatus() +
                '}';
    }
}