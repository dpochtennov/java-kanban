package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpicTask extends Task {
    private final List<UUID> subTaskIds = new ArrayList<>();

    public EpicTask(UUID id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public List<UUID> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId(UUID id) {
        if (!subTaskIds.contains(id)) {
            subTaskIds.add(id);
        }
    }

    public void removeSubTaskId(UUID id) {
        subTaskIds.remove(id);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
        super.setTaskStatus(TaskStatus.NEW);
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