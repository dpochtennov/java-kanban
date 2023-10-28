package manager;

/**
 * Managers class is the abstract factory for getting TaskManager object
 *
 * @author Dmitrii Pochtennov
 * @version 1.0
 * @since 2023-10-28
 */
public class Managers {

    /**
     * This method returns default InMemory task Manager
     *
     * @return TaskManager This method returns default InMemory task Manager
     */
    public static TaskManager getDefault() {
        HistoryManager inMemoryHistoryManager = getDefaultHistory();
        return new InMemoryTaskManager(inMemoryHistoryManager);
    }

    /**
     * This method returns default InMemory history Manager
     *
     * @return HistoryManager This method returns default InMemory task Manager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}