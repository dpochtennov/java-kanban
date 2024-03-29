package main.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    static SubTask subTask;
    static UUID subTaskId = UUID.randomUUID();
    static UUID epicId = UUID.randomUUID();
    static String subTaskName = "SomeName";
    static String subTaskDescription = "SomeDescription";
    static TaskStatus subTaskStatus = TaskStatus.NEW;

    @BeforeEach
    void beforeEach() {
        subTask = new SubTask(subTaskId, subTaskName, subTaskDescription, subTaskStatus, epicId);
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
                + subTaskDescription + "," + epicId;
        assertEquals(expectedString, subTask.toString());
    }
}