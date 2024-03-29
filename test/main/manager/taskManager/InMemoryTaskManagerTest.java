package main.manager.taskManager;

import main.tasks.EpicTask;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldReturnProperListOfSubtasksFromEpic() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        SubTask firstSubTask = manager.addSubTask(
                new SubTask("First Subtask", "Description", TaskStatus.NEW, epic.getId()));
        SubTask secondSubTask = manager.addSubTask(
                new SubTask("Second Subtask", "Description", TaskStatus.NEW, epic.getId()));
        List<SubTask> subtasks = manager.getSubtasksOfEpic(epic.getId());
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(firstSubTask));
        assertTrue(subtasks.contains(secondSubTask));
    }

    @Test
    void shouldReturnCorrectEpicStatus() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Epic Description"));
        manager.addSubTask(
                new SubTask("First Subtask", "Description", TaskStatus.DONE, epic.getId()));
        assertEquals(TaskStatus.DONE, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }
}