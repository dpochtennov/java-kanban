import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import tasks.*;

public class TaskManager {
    private final HashMap<UUID, Task> tasks = new HashMap<>();
    private final HashMap<UUID, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<UUID, SubTask> subTasks = new HashMap<>();

    public Task addTask(String name, String description, TaskStatus status) {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, name, description, status);
        tasks.put(id, task);
        return task;
    }

    public Task updateTask(UUID id, String name, String description, TaskStatus status) {
        Task task = tasks.get(id);
        task.setName(name);
        task.setDescription(description);
        task.setTaskStatus(status);
        return task;
    }

    public SubTask addSubTask(String name, String description, TaskStatus status, UUID epicId) {
        UUID id = UUID.randomUUID();
        SubTask subTask = new SubTask(id, name, description, status, epicId);
        subTasks.put(id, subTask);
        EpicTask epic = epicTasks.get(epicId);
        epic.addSubTaskId(id);
        recalculateEpicStatus(epic);
        return subTask;
    }

    public SubTask updateSubTask(UUID id, String name, String description, TaskStatus status, UUID epicId) {
        SubTask subTask = subTasks.get(id);
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setTaskStatus(status);

        EpicTask newEpic = epicTasks.get(epicId);
        if (!subTask.getEpicId().equals(epicId)) {
            EpicTask oldEpic = epicTasks.get(subTask.getEpicId());
            oldEpic.removeSubTaskId(id);
            newEpic.addSubTaskId(id);
        }

        recalculateEpicStatus(newEpic);
        return subTask;
    }


    public EpicTask addEpicTask(String name, String description, TaskStatus taskStatus, List<UUID> subTaskIds) {
        UUID id = UUID.randomUUID();
        EpicTask epic = new EpicTask(id, name, description, taskStatus, subTaskIds);
        epicTasks.put(id, epic);
        recalculateEpicStatus(epic);
        return epic;
    }

    public EpicTask updateEpicTask(UUID id, String name, String description, TaskStatus status,
                                   List<UUID> subTaskIds) {
        EpicTask epic = epicTasks.get(id);
        epic.setName(name);
        epic.setDescription(description);
        epic.setTaskStatus(status);
        if (!subTaskIds.equals(epic.getSubTaskIds())) {
            for (UUID subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epic.setSubTaskIds(subTaskIds);
        }

        recalculateEpicStatus(epic);
        return epic;
    }



    private void recalculateEpicStatus(EpicTask epic) {
        boolean isNew = true;
        boolean isDone = true;

        List<UUID> subTaskIds = epic.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        for (UUID subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            TaskStatus subTaskStatus = subTask.getTaskStatus();

            if (!subTaskStatus.equals(TaskStatus.NEW)) {
                isNew = false;
            }

            if (!subTaskStatus.equals(TaskStatus.DONE)) {
                isDone = false;
            }
        }

        if (isNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else if (isDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public HashMap<UUID, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<UUID, EpicTask> getAllEpics() {
        return epicTasks;
    }

    public HashMap<UUID, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public List<SubTask> getSubtasksOfEpic(UUID epicId) {
        List<SubTask> epicsSubTasks = new ArrayList<>();
        EpicTask epic = epicTasks.get(epicId);
        for (UUID subTaskId : epic.getSubTaskIds()) {
            epicsSubTasks.add(subTasks.get(subTaskId));
        }
        return epicsSubTasks;
    }

    public Task getAnyTaskById(UUID id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }

        if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        }

        return subTasks.get(id);
    }

    public void clearTaskLists() {
        tasks.clear();
        subTasks.clear();
        epicTasks.clear();
    }

    public void removeTaskById(UUID id) {
        tasks.remove(id);

        if (epicTasks.containsKey(id)) {
            EpicTask epic = epicTasks.get(id);
            for (UUID subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epicTasks.remove(id);
        }

        subTasks.remove(id);
    }


}