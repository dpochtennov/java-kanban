package main.manager.historyManager;

import main.manager.taskManager.TaskManager;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class HistoryManagerTest<T extends HistoryManager>  {

    protected static HistoryManager manager;

    @Test
    void add() {
        assertTrue(manager.getHistory().isEmpty());
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1));
        manager.add(task);
        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    void doNotAddIfExistsInHistory() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1));
        manager.add(task);
        manager.add(task);
        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    void removeFromBeginning() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1));
        Task secondTask = new Task(UUID.randomUUID(), "Second task", "Some task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1));
        Task thirdTask = new Task(UUID.randomUUID(), "Third task", "Some task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(1000), Duration.ofMinutes(1));
        manager.add(task);
        manager.add(secondTask);
        manager.add(thirdTask);

        manager.remove(task.getId());
        assertEquals(List.of(secondTask, thirdTask), manager.getHistory());
    }

    @Test
    void removeFromEnd() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1));
        Task secondTask = new Task(UUID.randomUUID(), "Second task", "Some task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1));
        Task thirdTask = new Task(UUID.randomUUID(), "Third task", "Some task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(1000), Duration.ofMinutes(1));
        manager.add(task);
        manager.add(secondTask);
        manager.add(thirdTask);

        manager.remove(thirdTask.getId());
        assertEquals(List.of(task, secondTask), manager.getHistory());
    }

    @Test
    void removeFromMid() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1));
        Task secondTask = new Task(UUID.randomUUID(), "Second task", "Some task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1));
        Task thirdTask = new Task(UUID.randomUUID(), "Third task", "Some task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(1000), Duration.ofMinutes(1));
        manager.add(task);
        manager.add(secondTask);
        manager.add(thirdTask);

        manager.remove(secondTask.getId());
        assertEquals(List.of(task, thirdTask), manager.getHistory());
    }

    @Test
    void getHistory() {
        Task task = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1));
        Task second = new Task(UUID.randomUUID(), "Second task", "Some second task", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1));
        manager.add(second);
        manager.add(task);
        assertEquals(List.of(second, task), manager.getHistory());
    }
}