package main.tasks;

import main.tasks.Task;
import main.tasks.TaskStatus;
import main.tasks.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    static Task task;
    static UUID taskId = UUID.randomUUID();
    static String taskName = "SomeName";
    static String taskDescription = "SomeDescription";
    static TaskStatus taskStatus = TaskStatus.NEW;
    static Duration taskDuration = Duration.ofMinutes(1);
    static LocalDateTime startTime = LocalDateTime.MIN;

    @BeforeEach
    void beforeEach() {
        task = new Task(taskId, taskName, taskDescription, taskStatus, startTime, taskDuration);
    }

    @Test
    void getName() {
        assertEquals(taskName, task.getName());
    }

    @Test
    void setName() {
        String expectedNewName = "NewName";
        task.setName(expectedNewName);
        assertEquals(expectedNewName, task.getName());
    }

    @Test
    void getDescription() {
        assertEquals(taskDescription, task.getDescription());
    }

    @Test
    void setDescription() {
        String expectedDescription = "NewDescription";
        task.setDescription(expectedDescription);
        assertEquals(expectedDescription, task.getDescription());
    }

    @Test
    void getId() {
        assertEquals(taskId, task.getId());
    }

    @Test
    void setId() {
        UUID newId = UUID.randomUUID();
        task.setId(newId);
        assertEquals(newId, task.getId());
    }

    @Test
    void getTaskStatus() {
        assertEquals(taskStatus, task.getTaskStatus());
    }

    @Test
    void setTaskStatus() {
        TaskStatus newStatus = TaskStatus.DONE;
        task.setTaskStatus(newStatus);
        assertEquals(newStatus, task.getTaskStatus());
    }

    @Test
    void getStartTime() {
        assertEquals(startTime, task.getStartTime());
    }

    @Test
    void setStartTime() {
        LocalDateTime newStartTime = LocalDateTime.MAX;
        task.setStartTime(newStartTime);
        assertEquals(newStartTime, task.getStartTime());
    }

    @Test
    void getDuration() {
        assertEquals(taskDuration, task.getDuration());
    }

    @Test
    void setTaskDuration() {
        Duration newTaskDuration = Duration.ofMinutes(200);
        task.setDuration(newTaskDuration);
        assertEquals(newTaskDuration, task.getDuration());
    }

    @Test
    void getEndTime() {
        LocalDateTime expectedEndTime = startTime.plus(taskDuration);
        assertEquals(expectedEndTime, task.getEndTime());
    }

    @Test
    void getType() {
        assertEquals(TaskTypes.TASK, task.getType());
    }

    @Test
    void testToString() {
        String expectedString = taskId + "," + TaskTypes.TASK + "," + taskName + "," + taskStatus + ","
                + taskDescription + "," + startTime + "," + taskDuration.toMinutes();
        assertEquals(expectedString, task.toString());
    }
}