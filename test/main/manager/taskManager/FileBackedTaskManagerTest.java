package main.manager.taskManager;

import main.customExceptions.ManagerReadException;
import main.tasks.EpicTask;
import main.tasks.SubTask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest {

    private File tmpFile;
    private static final String CSV_FILE_HEADER = "id,type,name,status,description,epic,start_time,duration_min\n";

    @BeforeEach
    void setUp() throws IOException {
        tmpFile = File.createTempFile("test", ".csv");
        Files.write(tmpFile.toPath(), List.of(CSV_FILE_HEADER), StandardCharsets.UTF_8);
        manager = FileBackedTaskManager.loadFromFile(tmpFile);
    }

    @Test
    public void shouldInitiateProperManagerStateFromFileContent() {
        manager.addTask(new Task("First task", "Description", TaskStatus.NEW, LocalDateTime.MIN,
                Duration.ofMinutes(1)));
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Description"));
        manager.addSubTask(new SubTask("First subtask", "Description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN.plusMinutes(100), Duration.ofMinutes(1)));

        manager.getEpicTaskById(epic.getId());

        TaskManager secondFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);

        assertEquals(1, secondFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, secondFileBackedTaskManager.getAllSubTasks().size());
        assertEquals(1, secondFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, secondFileBackedTaskManager.getHistory().size());
    }

    @Test
    public void shouldInitiateProperManagerStateAfterEmptyFileLoad() {
        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubTasks().size());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldReturnProperEpicTimeAfterFileLoad() {
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Description"));
        manager.addSubTask(new SubTask("First subtask", "Description", TaskStatus.NEW, epic.getId(),
                        LocalDateTime.MIN, Duration.ofMinutes(0)));
        manager.addSubTask(new SubTask("Second subtask", "Description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MAX.minusMinutes(1), Duration.ofMinutes(1)));
        manager.getEpicTaskById(epic.getId());

        TaskManager secondFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);

        EpicTask retrievedEpic = secondFileBackedTaskManager.getEpicTaskById(epic.getId());
        assertEquals(Duration.between(LocalDateTime.MIN, LocalDateTime.MAX), retrievedEpic.getDuration());
        assertEquals(LocalDateTime.MIN, retrievedEpic.getStartTime());
        assertEquals(LocalDateTime.MAX, retrievedEpic.getEndTime());
    }

    @Test
    public void shouldThrowWhenInvalidFileContent() {
        assertThrows(ManagerReadException.class, () -> {
            File newTempFile = File.createTempFile("test", ".csv");
            manager = FileBackedTaskManager.loadFromFile(newTempFile);;
        });
    }
}