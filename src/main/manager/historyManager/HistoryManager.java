package main.manager.historyManager;

import main.tasks.Task;

import java.util.List;
import java.util.UUID;

/**
 * Interface for History Manager
 *
 * @author Dmitrii Pochtennov
 * @version 1.0
 * @since 2023-10-28
 */
public interface HistoryManager {
    /**
     * This method adds the task to the task history.
     *
     * @param task task to add.
     */
    void add(Task task);

    /**
     * This method returns the task history.
     *
     * @return List<Task> returns main.tasks which were viewed by user.
     */
    List<Task> getHistory();

    /**
     * This method removes task with a provided id from history.
     *
     * @param id id of the task to remove.
     * @return does not return anything.
     */
    void remove(UUID id);
}
