package main.manager.historyManager;

import main.manager.taskManager.TaskManager;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class HistoryManagerTest {

    protected static HistoryManager manager;

    @Test
    void add() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW);
        manager.add(task);
        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    void remove() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW);
        manager.add(task);
        assertEquals(List.of(task), manager.getHistory());
        manager.remove(task.getId());
        assertEquals(List.of(), manager.getHistory());
    }

    @Test
    void getHistory() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW);
        Task second = new Task(UUID.randomUUID(), "Second task", "Some second task", TaskStatus.NEW);
        manager.add(second);
        manager.add(task);
        assertEquals(List.of(second, task), manager.getHistory());
    }
}