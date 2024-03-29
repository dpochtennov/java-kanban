package main.manager.historyManager;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends HistoryManagerTest {

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
    }
}