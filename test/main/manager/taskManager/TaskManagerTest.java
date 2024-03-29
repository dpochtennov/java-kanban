package main.manager.taskManager;

import main.tasks.EpicTask;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {

    protected static TaskManager manager;

    @Test
    void addTask() {
        Task addedTask = manager.addTask(new Task("First task", "Some first task", TaskStatus.NEW));
        assertEquals(List.of(addedTask), manager.getAllTasks());
    }

    @Test
    void updateTask() {
        Task addedTask = manager.addTask(new Task("First task", "Some first task", TaskStatus.NEW));
        String expectedNewDescription = "Updated task";
        addedTask.setDescription(expectedNewDescription);
        manager.updateTask(addedTask);
        assertEquals(List.of(addedTask), manager.getAllTasks());
        assertEquals(expectedNewDescription, manager.getTaskById(addedTask.getId()).getDescription());
    }

    @Test
    void doNotUpdateNonAddedTask() {
        Task nonAddedTask = new Task(
                UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW);
        manager.updateTask(nonAddedTask);
        assertEquals(List.of(), manager.getAllTasks());
    }

    @Test
    void removeTaskById() {
        Task addedTask = manager.addTask(new Task("First task", "Some first task", TaskStatus.NEW));
        assertEquals(List.of(addedTask), manager.getAllTasks());
        manager.removeTaskById(addedTask.getId());
        assertEquals(List.of(), manager.getAllTasks());
    }

    @Test
    void clearTaskList() {
        manager.addTask(new Task("First task", "Some first task", TaskStatus.NEW));
        manager.addTask(new Task("Second task", "Some first task", TaskStatus.NEW));
        manager.clearTaskList();
        assertEquals(List.of(), manager.getAllTasks());
    }

    @Test
    void getTaskById() {
        Task addedTask = manager.addTask(new Task("First task", "Some first task", TaskStatus.NEW));
        assertEquals(addedTask, manager.getTaskById(addedTask.getId()));
    }

    @Test
    void getAllTasks() {
        Task firstAddedTask = manager.addTask(new Task("First task", "Some first task", TaskStatus.NEW));
        Task secondAddedTask = manager.addTask(
                new Task("Second task", "Some first task", TaskStatus.NEW));
        List<Task> expected = List.of(firstAddedTask, secondAddedTask);
        assertTrue(expected.containsAll(manager.getAllTasks()));
    }

    @Test
    void addSubTask() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        SubTask addedSubTask = manager.addSubTask(
                new SubTask("First sub task", "Some first subtask", TaskStatus.NEW, epicTask.getId()));
        assertEquals(addedSubTask, manager.getSubTaskById(addedSubTask.getId()));
    }

    @Test
    void updateSubTask() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        SubTask addedSubTask = manager.addSubTask(
                new SubTask("First sub task", "Some first subtask", TaskStatus.NEW, epicTask.getId()));
        addedSubTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(addedSubTask);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubTaskById(addedSubTask.getId()).getTaskStatus());
    }

    @Test
    void removeSubTaskById() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        SubTask addedSubTask = manager.addSubTask(
                new SubTask("First sub task", "Some first subtask", TaskStatus.NEW, epicTask.getId()));
        manager.removeSubTaskById(addedSubTask.getId());
        assertEquals(List.of(), manager.getAllSubTasks());
    }

    @Test
    void clearSubTaskLists() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        manager.addSubTask(
                new SubTask("First sub task", "First subtask", TaskStatus.NEW, epicTask.getId()));
        manager.addSubTask(
                new SubTask("Second subtask", "Second subtask", TaskStatus.NEW, epicTask.getId()));
        manager.clearSubTaskLists();
        assertEquals(List.of(), manager.getAllSubTasks());
    }

    @Test
    void getSubTaskById() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        SubTask addedSubTask = manager.addSubTask(
                new SubTask("First sub task", "Some first subtask", TaskStatus.NEW, epicTask.getId()));
        assertEquals(addedSubTask, manager.getSubTaskById(addedSubTask.getId()));
    }

    @Test
    void getAllSubTasks() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        SubTask first = manager.addSubTask(
                new SubTask("First sub task", "First subtask", TaskStatus.NEW, epicTask.getId()));
        SubTask second = manager.addSubTask(
                new SubTask("Second subtask", "Second subtask", TaskStatus.NEW, epicTask.getId()));
        assertTrue(List.of(first, second).containsAll(manager.getAllSubTasks()));
    }

    @Test
    void addEpicTask() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        assertTrue(List.of(epicTask).containsAll(manager.getAllEpics()));
    }

    @Test
    void updateEpicTask() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        String updatedDescription = "Updated";
        epicTask.setDescription(updatedDescription);
        manager.updateEpicTask(epicTask);
        assertEquals(updatedDescription, manager.getEpicTaskById(epicTask.getId()).getDescription());
    }

    @Test
    void removeEpicTaskById() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        assertTrue(List.of(epicTask).containsAll(manager.getAllEpics()));
        manager.removeEpicTaskById(epicTask.getId());
        assertTrue(List.of().containsAll(manager.getAllEpics()));
    }

    @Test
    void clearEpicTaskLists() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        EpicTask secondEpic = manager.addEpicTask(new EpicTask("Second Epic", "Second Epic"));
        assertTrue(List.of(epicTask, secondEpic).containsAll(manager.getAllEpics()));
        manager.clearEpicTaskLists();
        assertTrue(List.of().containsAll(manager.getAllEpics()));
    }

    @Test
    void getEpicTaskById() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        assertEquals(epicTask, manager.getEpicTaskById(epicTask.getId()));
    }

    @Test
    void getSubtasksOfEpic() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        assertEquals(List.of(), manager.getSubtasksOfEpic(epicTask.getId()));
        SubTask first = manager.addSubTask(
                new SubTask("First sub task", "First subtask", TaskStatus.NEW, epicTask.getId()));
        SubTask second = manager.addSubTask(
                new SubTask("Second subtask", "Second subtask", TaskStatus.NEW, epicTask.getId()));
        assertEquals(List.of(first, second), manager.getSubtasksOfEpic(epicTask.getId()));
    }

    @Test
    void getAllEpics() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        EpicTask secondEpic = manager.addEpicTask(new EpicTask("Second Epic", "Second Epic"));
        assertTrue(List.of(epicTask, secondEpic).containsAll(manager.getAllEpics()));
    }

    @Test
    void getHistory() {
        EpicTask epicTask = manager.addEpicTask(new EpicTask("First Epic", "First Epic description"));
        Task task = manager.addTask(new Task("Second Epic", "Second Epic", TaskStatus.NEW));
        SubTask subTask =  manager.addSubTask(
                new SubTask("First sub task", "First subtask", TaskStatus.NEW, epicTask.getId()));
        manager.getTaskById(task.getId());
        manager.getSubTaskById(subTask.getId());
        manager.getEpicTaskById(epicTask.getId());
        assertEquals(List.of(task, subTask, epicTask), manager.getHistory());
        manager.clearTaskList();
        assertEquals(List.of(subTask, epicTask), manager.getHistory());
        manager.clearSubTaskLists();
        assertEquals(List.of(epicTask), manager.getHistory());
        manager.clearEpicTaskLists();
        assertEquals(List.of(), manager.getHistory());
    }
}