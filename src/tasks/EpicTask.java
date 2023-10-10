package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpicTask extends Task {
    private List<UUID> subTaskIds;

    public EpicTask(UUID id, String name, String description, TaskStatus taskStatus, List<UUID> subTaskIds) {
        super(id, name, description, taskStatus);
        this.subTaskIds = subTaskIds;
    }

    public EpicTask(UUID id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.subTaskIds = new ArrayList<>();
    }

    public List<UUID> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<UUID> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void addSubTaskId(UUID id) {
        if (!subTaskIds.contains(id)) {
            subTaskIds.add(id);
        }
    }

    public void removeSubTaskId(UUID id) {
        subTaskIds.remove(id);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id='" + super.getId() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", subTasksIds='" + subTaskIds + '\'' +
                ", taskStatus=" + super.getTaskStatus() +
                '}';
    }

}