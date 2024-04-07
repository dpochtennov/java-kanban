package main.manager.taskManager;

import main.customExceptions.NoSubTaskEpicException;
import main.customExceptions.TaskIntersectedException;
import main.manager.Managers;
import main.manager.historyManager.HistoryManager;
import main.tasks.EpicTask;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<UUID, Task> tasks = new HashMap<>();
    private final Map<UUID, EpicTask> epicTasks = new HashMap<>();
    private final Map<UUID, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Comparator<Task> comparator = Comparator.comparing(
        Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())
    );
    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    @Override
    public Task addTask(Task task) {

        if (isTaskIntersected(task)) {
            throw new TaskIntersectedException("Task times intersect with already added tasks");
        }

        UUID id = task.getId();
        if (id == null) {
            id = UUID.randomUUID();
            task.setId(id);
        }
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        removePrioritizedTaskById(task.getId());
        if (isTaskIntersected(task)) {
            throw new TaskIntersectedException("Task times intersect with already added tasks");
        }

        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public void removeTaskById(UUID id) {
        if (id != null) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearTaskList() {
        for (Map.Entry<UUID, Task> taskEntry : tasks.entrySet()) {
            historyManager.remove(taskEntry.getKey());
            prioritizedTasks.remove(taskEntry.getValue());
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

        if (isTaskIntersected(subTask)) {
            throw new TaskIntersectedException("Task times intersect with already added tasks");
        }

        UUID subTaskId = subTask.getId();
        if (subTaskId == null) {
            subTaskId = UUID.randomUUID();
            subTask.setId(subTaskId);
        }
        EpicTask epic = epicTasks.get(subTask.getEpicId());
        if (epic != null) {
            subTasks.put(subTaskId, subTask);
            epic.addSubTaskId(subTaskId);
            recalculateEpicData(epic);
            prioritizedTasks.add(subTask);
        } else {
            throw new NoSubTaskEpicException("There is no epic for this subtask!");
        }
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {

        removePrioritizedTaskById(subTask.getId());

        if (isTaskIntersected(subTask)) {
            return subTask;
        }

        UUID subTaskId = subTask.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, subTask);
        }
        EpicTask epic = epicTasks.get(subTask.getEpicId());
        if (epic != null) {
            recalculateEpicData(epic);
        } else {
            throw new RuntimeException("There is no epic for this subtask!");
        }
        prioritizedTasks.add(subTask);
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
                recalculateEpicData(relatedEpic);
            }
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearSubTaskLists() {
        for (Map.Entry<UUID, SubTask> subTaskEntry : subTasks.entrySet()) {
            historyManager.remove(subTaskEntry.getKey());
            prioritizedTasks.remove(subTaskEntry.getValue());
        }
        subTasks.clear();
        for (EpicTask epic : epicTasks.values()) {
            epic.clearSubTaskIds();
            recalculateEpicData(epic);
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
                prioritizedTasks.remove(subTasks.get(subTaskId));
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epicTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearEpicTaskLists() {
        subTasks.forEach((key, value) -> {
            historyManager.remove(key);
            prioritizedTasks.remove(value);
        });
        epicTasks.keySet().forEach(historyManager::remove);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    private void recalculateEpicData(EpicTask epic) {
        recalculateEpicStatus(epic);
        recalculateEpicTimeData(epic);
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

    private void recalculateEpicTimeData(EpicTask epic) {

        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        List<SubTask> subTasksOfEpic = epic.getSubTaskIds().stream().map(subTasks::get).collect(Collectors.toList());

        LocalDateTime minStartTime = subTasksOfEpic.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElseThrow(NoSuchElementException::new);
        LocalDateTime maxEndTime = subTasksOfEpic.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElseThrow(NoSuchElementException::new);
        Duration duration = subTasksOfEpic.stream().map(SubTask::getDuration).reduce(Duration.ZERO, Duration::plus);
        epic.setStartTime(minStartTime);
        epic.setEndTime(maxEndTime);
        epic.setDuration(duration);
    }

    private boolean isTaskIntersected(Task task) {
        return prioritizedTasks.stream()
            .anyMatch(prioritizedTask -> {
                LocalDateTime startTime = prioritizedTask.getStartTime();
                LocalDateTime endTime = prioritizedTask.getEndTime();
                return (
                    task.getStartTime().equals(startTime) ||
                    task.getEndTime().equals(endTime) ||
                    (task.getStartTime().isAfter(startTime) && task.getEndTime().isBefore(endTime)) ||
                    (task.getStartTime().isBefore(startTime) && task.getEndTime().isAfter(endTime)) ||
                    (task.getStartTime().isBefore(startTime) && task.getEndTime().isAfter(startTime)) ||
                    (task.getStartTime().isAfter(startTime) && task.getStartTime().isBefore(endTime))
                );
            });
    }

    private void removePrioritizedTaskById(UUID id) {
        Optional<Task> taskBeforeUpdate = prioritizedTasks.stream()
                .filter(nonUpdatedTask -> nonUpdatedTask.getId().equals(id)).findFirst();
        taskBeforeUpdate.ifPresent(prioritizedTasks::remove);
    }
}