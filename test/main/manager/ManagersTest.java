package main.manager;

import main.manager.historyManager.HistoryManager;
import main.manager.historyManager.InMemoryHistoryManager;
import main.manager.taskManager.InMemoryTaskManager;
import main.manager.taskManager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager defaultManager = Managers.getDefault();
        assertInstanceOf(InMemoryTaskManager.class, defaultManager);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager defaultHistoryManager = Managers.getDefaultHistory();
        assertInstanceOf(InMemoryHistoryManager.class, defaultHistoryManager);
    }
}