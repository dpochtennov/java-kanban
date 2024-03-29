package main.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    static EpicTask epicTask;
    static UUID epicTaskId = UUID.randomUUID();
    static String epicTaskName = "SomeName";
    static String epicTaskDescription = "SomeDescription";

    @BeforeEach
    void beforeEach() {
        epicTask = new EpicTask(epicTaskId, epicTaskName, epicTaskDescription);
    }

    @Test
    void getSubTaskIds() {
        assertEquals(List.of(), epicTask.getSubTaskIds());
    }

    @Test
    void addSubTaskId() {
        assertEquals(List.of(), epicTask.getSubTaskIds());
        UUID randomSubtaskId = UUID.randomUUID();
        epicTask.addSubTaskId(randomSubtaskId);
        assertEquals(List.of(randomSubtaskId), epicTask.getSubTaskIds());
    }

    @Test
    void removeSubTaskId() {
        assertEquals(List.of(), epicTask.getSubTaskIds());
        UUID randomSubtaskId = UUID.randomUUID();
        epicTask.addSubTaskId(randomSubtaskId);
        assertEquals(List.of(randomSubtaskId), epicTask.getSubTaskIds());
        epicTask.removeSubTaskId(randomSubtaskId);
        assertEquals(List.of(), epicTask.getSubTaskIds());
    }

    @Test
    void clearSubTaskIds() {
        UUID randomSubtaskId = UUID.randomUUID();
        epicTask.addSubTaskId(randomSubtaskId);
        assertEquals(List.of(randomSubtaskId), epicTask.getSubTaskIds());
        epicTask.clearSubTaskIds();
        assertEquals(List.of(), epicTask.getSubTaskIds());
    }

    @Test
    void getType() {
        assertEquals(TaskTypes.EPIC, epicTask.getType());
    }
}