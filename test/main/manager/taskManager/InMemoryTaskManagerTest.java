package main.manager.taskManager;

import main.tasks.EpicTask;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldReturnProperListOfSubtasksFromEpic() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        SubTask firstSubTask = manager.addSubTask(new SubTask("First Subtask", "Description",
                TaskStatus.NEW, epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        SubTask secondSubTask = manager.addSubTask(new SubTask("Second Subtask", "Description",
                TaskStatus.NEW, epic.getId(), LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1)));
        List<SubTask> subtasks = manager.getSubtasksOfEpic(epic.getId());
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(firstSubTask));
        assertTrue(subtasks.contains(secondSubTask));
    }

    @Test
    void shouldReturnCorrectEpicStatusWhenAllNew() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        manager.addSubTask(new SubTask("First Subtask", "Description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        manager.addSubTask(new SubTask("Second Subtask", "Description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1)));
        assertEquals(TaskStatus.NEW, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    void shouldReturnCorrectEpicStatusWhenAllDone() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        manager.addSubTask(new SubTask("First Subtask", "Description", TaskStatus.DONE, epic.getId(),
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        manager.addSubTask(new SubTask("Second Subtask", "Description", TaskStatus.DONE, epic.getId(),
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1)));
        assertEquals(TaskStatus.DONE, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    void shouldReturnCorrectEpicStatusWhenDoneAndNew() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        manager.addSubTask(new SubTask("First Subtask", "Description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        manager.addSubTask(new SubTask("Second Subtask", "Description", TaskStatus.DONE, epic.getId(),
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1)));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    void shouldReturnCorrectEpicStatusWhenInProgress() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        manager.addSubTask(new SubTask("First Subtask", "Description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        manager.addSubTask(new SubTask("Second Subtask", "Description", TaskStatus.IN_PROGRESS,
                epic.getId(), LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1)));
        manager.addSubTask(new SubTask("Third Subtask", "Description", TaskStatus.DONE, epic.getId(),
                LocalDateTime.MIN.plusMinutes(1000), Duration.ofMinutes(1)));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }
}