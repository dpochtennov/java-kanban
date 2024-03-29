package main.manager.taskManager;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest {

    private File tmpFile;
    private static final String CSV_FILE_HEADER = "id,type,name,status,description,epic\n";

    @BeforeEach
    void setUp() throws IOException {
        tmpFile = File.createTempFile("test", ".csv");
        Files.write(tmpFile.toPath(), List.of(CSV_FILE_HEADER), StandardCharsets.UTF_8);
        manager = FileBackedTaskManager.loadFromFile(tmpFile);
    }

    @Test
    public void shouldInitiateProperManagerStateFromFileContent() {
        manager.addTask(new Task("First task", "Description", TaskStatus.NEW));
        EpicTask epic = manager.addEpicTask(new EpicTask("First Epic", "Description"));
        manager.addSubTask(
                new SubTask("First subtask", "Description", TaskStatus.NEW, epic.getId()));

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
}