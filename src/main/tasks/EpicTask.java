package main.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpicTask extends Task {
    private List<UUID> subTaskIds = new ArrayList<>();

    public EpicTask(UUID id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.type = TaskTypes.EPIC;
    }

    public EpicTask(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.type = TaskTypes.EPIC;
    }

    public EpicTask(UUID id, String name, String description, TaskStatus taskStatus, List<UUID> subTaskIds) {
        super(id, name, description, taskStatus);
        this.subTaskIds = subTaskIds;
        this.type = TaskTypes.EPIC;
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
}