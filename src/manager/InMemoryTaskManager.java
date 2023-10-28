package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<UUID, Task> tasks = new HashMap<>();
    private final HashMap<UUID, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<UUID, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task addTask(Task task) {
        UUID id = UUID.randomUUID();
        task.setId(id);
        tasks.put(task.getId(), task);
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
        }
    }

    @Override
    public void clearTaskList() {
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
        UUID subTaskId = UUID.randomUUID();
        subTask.setId(subTaskId);
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
        }
    }

    @Override
    public void clearSubTaskLists() {
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
        UUID id = UUID.randomUUID();
        epicTask.setId(id);
        epicTasks.put(epicTask.getId(), epicTask);
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
            }
            epicTasks.remove(id);
        }
    }

    @Override
    public void clearEpicTaskLists() {
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