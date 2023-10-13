package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TaskManager {
    private final HashMap<UUID, Task> tasks = new HashMap<>();
    private final HashMap<UUID, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<UUID, SubTask> subTasks = new HashMap<>();

    public Task addTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public void removeTaskById(UUID id) {
        if (id != null) {
            tasks.remove(id);
        }
    }

    public void clearTaskList() {
        tasks.clear();
    }

    public Task getTaskById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Provided id is null");
        }
        return tasks.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public SubTask addSubTask(SubTask subTask) {
        UUID subTaskId = subTask.getId();
        subTasks.put(subTaskId, subTask);
        EpicTask epic = epicTasks.get(subTask.getEpicId());
        epic.addSubTaskId(subTaskId);
        return subTask;
    }

    public SubTask updateSubTask(SubTask subTask) {
        UUID subTaskId = subTask.getId();
        SubTask nonModifiedSubTask = subTasks.get(subTaskId);
        subTasks.put(subTaskId, subTask);

        EpicTask newEpic = epicTasks.get(subTask.getEpicId());
        if (!nonModifiedSubTask.getEpicId().equals(subTask.getEpicId())) {
            EpicTask oldEpic = epicTasks.get(nonModifiedSubTask.getEpicId());
            oldEpic.removeSubTaskId(subTaskId);
            newEpic.addSubTaskId(subTaskId);
        }

        recalculateEpicStatus(newEpic);
        return subTask;
    }

    public void removeSubTaskById(UUID id) {
        if (id != null && subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            UUID relatedEpicId = subTask.getEpicId();
            if (epicTasks.containsKey(relatedEpicId)) {
                EpicTask relatedEpic = epicTasks.get(relatedEpicId);
                relatedEpic.removeSubTaskId(id);
            }
            subTasks.remove(id);
        }
    }

    public void clearSubTaskLists() {
        for (UUID id : subTasks.keySet()) {
            removeSubTaskById(id);
        }
    }

    public SubTask getSubTaskById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Provided id is null");
        }
        return subTasks.get(id);
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public EpicTask addEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask;
    }

    public EpicTask updateEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask;
    }

    public void removeEpicTaskById(UUID id) {
        if (id != null && epicTasks.containsKey(id)) {
            EpicTask epic = epicTasks.get(id);
            for (UUID subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epicTasks.remove(id);
        }
    }

    public void clearEpicTaskLists() {
        for (UUID id : epicTasks.keySet()) {
            removeEpicTaskById(id);
        }
    }

    public EpicTask getEpicTaskById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Provided id is null");
        }
        EpicTask epic = epicTasks.get(id);
        recalculateEpicStatus(epic);
        return epic;
    }

    public List<SubTask> getSubtasksOfEpic(UUID epicId) {
        if (epicId == null) {
            return new ArrayList<>();
        }
        List<SubTask> epicsSubTasks = new ArrayList<>();
        EpicTask epic = epicTasks.get(epicId);
        for (UUID subTaskId : epic.getSubTaskIds()) {
            epicsSubTasks.add(subTasks.get(subTaskId));
        }
        return epicsSubTasks;
    }

    public List<EpicTask> getAllEpics() {
        List<EpicTask> epics = new ArrayList<>();
        for (UUID id : epicTasks.keySet()) {
            EpicTask epicTask = getEpicTaskById(id);
            epics.add(epicTask);
        }
        return epics;
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
}