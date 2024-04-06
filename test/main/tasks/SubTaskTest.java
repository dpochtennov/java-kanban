package main.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    static SubTask subTask;
    static UUID subTaskId = UUID.randomUUID();
    static UUID epicId = UUID.randomUUID();
    static String subTaskName = "SomeName";
    static String subTaskDescription = "SomeDescription";
    static TaskStatus subTaskStatus = TaskStatus.NEW;
    static Duration taskDuration = Duration.ofHours(1);
    static LocalDateTime startTime = LocalDateTime.MIN;

    @BeforeEach
    void beforeEach() {
        subTask = new SubTask(subTaskId, subTaskName, subTaskDescription, subTaskStatus, epicId, startTime, taskDuration);
    }

    @Test
    void getEpicId() {
        assertEquals(epicId, subTask.getEpicId());
    }

    @Test
    void getType() {
        assertEquals(TaskTypes.SUBTASK, subTask.getType());
    }

    @Test
    void testToString() {
        String expectedString = subTaskId + "," + TaskTypes.SUBTASK + "," + subTaskName + "," + subTaskStatus + ","
                + subTaskDescription + "," + startTime + "," + taskDuration.toMinutes() + "," + epicId;
        assertEquals(expectedString, subTask.toString());
    }
}