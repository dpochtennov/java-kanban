package main.manager.taskManager;

import main.manager.Managers;
import main.manager.historyManager.HistoryManager;
import main.tasks.EpicTask;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InMemoryTaskManager implements TaskManager {
    private final Map<UUID, Task> tasks = new HashMap<>();
    private final Map<UUID, EpicTask> epicTasks = new HashMap<>();
    private final Map<UUID, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task addTask(Task task) {
        UUID id = task.getId();
        if (id == null) {
            id = UUID.randomUUID();
            task.setId(id);
        }
        tasks.put(id, task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public void removeTaskById(UUID id) {
        if (id != null) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearTaskList() {
        for (UUID taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Provided id is null");
        }
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        UUID subTaskId = subTask.getId();
        if (subTaskId == null) {
            subTaskId = UUID.randomUUID();
            subTask.setId(subTaskId);
        }
        subTasks.put(subTaskId, subTask);
        EpicTask epic = epicTasks.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subTaskId);
            recalculateEpicStatus(epic);
        } else {
            throw new RuntimeException("There is no epic for this subtask!");
        }
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        UUID subTaskId = subTask.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, subTask);
        }
        EpicTask epic = epicTasks.get(subTask.getEpicId());
        if (epic != null) {
            recalculateEpicStatus(epic);
        } else {
            throw new RuntimeException("There is no epic for this subtask!");
        }
        return subTask;
    }

    @Override
    public void removeSubTaskById(UUID id) {
        if (id != null && subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            UUID relatedEpicId = subTask.getEpicId();
            if (epicTasks.containsKey(relatedEpicId)) {
                EpicTask relatedEpic = epicTasks.get(relatedEpicId);
                relatedEpic.removeSubTaskId(id);
                recalculateEpicStatus(relatedEpic);
            }
            subTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearSubTaskLists() {
        for (UUID taskId : subTasks.keySet()) {
            historyManager.remove(taskId);
        }
        subTasks.clear();
        for (EpicTask epic : epicTasks.values()) {
            epic.clearSubTaskIds();
            recalculateEpicStatus(epic);
        }
    }

    @Override
    public SubTask getSubTaskById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Provided id is null");
        }
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public EpicTask addEpicTask(EpicTask epicTask) {
        UUID epicTaskId = epicTask.getId();
        if (epicTaskId == null) {
            epicTaskId = UUID.randomUUID();
            epicTask.setId(epicTaskId);
        }
        epicTasks.put(epicTaskId, epicTask);
        return epicTask;
    }

    @Override
    public EpicTask updateEpicTask(EpicTask epicTask) {
        if (epicTasks.containsKey(epicTask.getId())) {
            epicTasks.put(epicTask.getId(), epicTask);
        }
        return epicTask;
    }

    @Override
    public void removeEpicTaskById(UUID id) {
        if (id != null && epicTasks.containsKey(id)) {
            EpicTask epic = epicTasks.get(id);
            for (UUID subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epicTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearEpicTaskLists() {
        Set<UUID> idsToDeleteFromHistory = new HashSet<>();
        idsToDeleteFromHistory.addAll(subTasks.keySet());
        idsToDeleteFromHistory.addAll(epicTasks.keySet());
        for (UUID taskId : idsToDeleteFromHistory) {
            historyManager.remove(taskId);
        }
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public EpicTask getEpicTaskById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Provided id is null");
        }
        EpicTask epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
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

    @Override
    public List<EpicTask> getAllEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Task getAnyTaskById(UUID id) {
        Task requiredTask;
        if (tasks.containsKey(id)) {
            requiredTask = tasks.get(id);
        } else if (epicTasks.containsKey(id)) {
            requiredTask = epicTasks.get(id);
        } else {
            requiredTask = subTasks.get(id);
        }
        historyManager.add(requiredTask);
        return requiredTask;
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