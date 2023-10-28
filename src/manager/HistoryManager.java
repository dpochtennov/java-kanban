package manager;

import tasks.Task;

import java.util.List;

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
     * @return List<Task> returns tasks which were viewed by user.
     */
    List<Task> getHistory();
}
