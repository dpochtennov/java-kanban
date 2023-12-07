package manager.taskManager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.List;
import java.util.UUID;

/**
 * Interface for Task Manager
 *
 * @author Dmitrii Pochtennov
 * @version 1.0
 * @since 2023-10-28
 */
public interface TaskManager {

    /**
     * This method adds Task to the current task storage.
     *
     * @param task Task object.
     * @return Task returns saved to current storage Task object.
     */
    Task addTask(Task task);

    /**
     * This method updates Task in the current task storage.
     *
     * @param task Task object.
     * @return Task returns updated in the current storage Task object.
     */
    Task updateTask(Task task);

    /**
     * This method removes Task by its ID from the current task storage.
     *
     * @param id id of the task.
     */
    void removeTaskById(UUID id);

    /**
     * Clears current task storage.
     */
    void clearTaskList();

    /**
     * This method returns Task by its ID from the current task storage.
     *
     * @param id id of the task.
     * @return Task by its id.
     */
    Task getTaskById(UUID id);

    /**
     * This method returns all tasks from the current task storage.
     *
     * @return List<Task> list of tasks.
     */
    List<Task> getAllTasks();

    /**
     * This method adds SubTask to the current SubTask storage.
     *
     * @param subTask SubTask object.
     * @return SubTask returns saved to current storage SubTask object.
     */
    SubTask addSubTask(SubTask subTask);

    /**
     * This method updates SubTask in the current SubTask storage.
     *
     * @param subTask SubTask object.
     * @return SubTask returns updated in the current storage SubTask object.
     */
    SubTask updateSubTask(SubTask subTask);

    /**
     * This method removes SubTask by its ID from the current SubTask storage.
     *
     * @param id id of the task.
     */
    void removeSubTaskById(UUID id);

    /**
     * Clears current SubTask storage.
     */
    void clearSubTaskLists();

    /**
     * This method returns SubTask by its ID from the current task storage.
     *
     * @param id id of the SubTask.
     * @return SubTask by its id.
     */
    SubTask getSubTaskById(UUID id);

    /**
     * This method returns all SubTasks from the current SubTask storage.
     *
     * @return List<SubTask> list of subtasks.
     */
    List<SubTask> getAllSubTasks();

    /**
     * This method adds EpicTask to the current task storage.
     *
     * @param epicTask EpicTask object.
     * @return EpicTask returns saved to current storage EpicTask object.
     */
    EpicTask addEpicTask(EpicTask epicTask);

    /**
     * This method updates EpicTask in the current task storage.
     *
     * @param epicTask EpicTask object.
     * @return EpicTask returns updated in the current storage EpicTask object.
     */
    EpicTask updateEpicTask(EpicTask epicTask);

    /**
     * This method removes EpicTask by its ID from the current EpicTask storage.
     *
     * @param id id of the EpicTask.
     */
    void removeEpicTaskById(UUID id);

    /**
     * Clears current EpicTask storage.
     */
    void clearEpicTaskLists();

    /**
     * This method returns EpicTask by its ID from the current EpicTask storage.
     *
     * @param id id of the EpicTask.
     * @return EpicTask by its id.
     */
    EpicTask getEpicTaskById(UUID id);

    /**
     * This method returns all SubTasks for the EpicTask by epicId.
     *
     * @param epicId id of the EpicTask
     * @return List<SubTask> list of subtasks of the epic.
     */
    List<SubTask> getSubtasksOfEpic(UUID epicId);

    /**
     * This method returns all EpicTasks from the current EpicTask storage.
     *
     * @return List<EpicTask> list of epics.
     */
    List<EpicTask> getAllEpics();

    /**
     * Returns history of 10 last task retrieval operations.
     *
     * @return List<Task> history of last 10 retrieval operations.
     */
    List<Task> getHistory();
}
