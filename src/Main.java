import manager.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        TaskManager testManager = new TaskManager();

        Task firstTask = new Task(UUID.randomUUID(), "First task", "Some first task", TaskStatus.NEW);
        Task secondTask = new Task(UUID.randomUUID(), "Second task", "Some second task",
                TaskStatus.NEW);
        EpicTask firstEpic = new EpicTask(UUID.randomUUID(), "First epic", "Some first Epic");
        EpicTask secondEpic = new EpicTask(UUID.randomUUID(), "Second epic", "Some second Epic");
        SubTask firstSubTask = new SubTask(UUID.randomUUID(), "First subtask",
                "FirstSubTask description", TaskStatus.NEW, firstEpic.getId());
        SubTask secondSubTask = new SubTask(UUID.randomUUID(), "Second subtask",
                "SecondSubTask description", TaskStatus.NEW, firstEpic.getId());
        SubTask thirdSubTask = new SubTask(UUID.randomUUID(), "Third subtask",
                "ThirdSubTask description", TaskStatus.NEW, secondEpic.getId());

        testManager.addTask(firstTask);
        testManager.addTask(secondTask);
        testManager.addEpicTask(firstEpic);
        testManager.addEpicTask(secondEpic);
        testManager.addSubTask(firstSubTask);
        testManager.addSubTask(secondSubTask);
        testManager.addSubTask(thirdSubTask);

        System.out.println("Check get task list methods");
        System.out.println("Tasks: " + testManager.getAllTasks());
        System.out.println("Epics: " + testManager.getAllEpics());
        System.out.println("Subtasks: " + testManager.getAllSubTasks());

        System.out.println();
        System.out.println("Check updateTask method");
        Task firstTaskUpdated = new Task(firstTask.getId(), "First task updated",
                "Some first task updated", TaskStatus.DONE);
        Task secondTaskUpdated = new Task(secondTask.getId(), "Second task updated",
                "Some second task updated", TaskStatus.IN_PROGRESS);
        testManager.updateTask(firstTaskUpdated);
        testManager.updateTask(secondTaskUpdated);
        System.out.println("Tasks: " + testManager.getAllTasks());

        System.out.println();
        System.out.println("Check updateSubTask method");
        SubTask firstSubTaskUpdated = new SubTask(firstSubTask.getId(), "First subtask updated",
                "FirstSubTask description updated", TaskStatus.IN_PROGRESS, firstEpic.getId());
        SubTask secondSubTaskUpdated = new SubTask(secondSubTask.getId(), "Second subTask updated",
                "SecondSubTask description updated", TaskStatus.DONE, firstEpic.getId());
        testManager.updateSubTask(firstSubTaskUpdated);
        testManager.updateSubTask(secondSubTaskUpdated);
        System.out.println("SubTasks: " + testManager.getAllSubTasks());

        System.out.println();
        System.out.println("Check updateEpicTask method");
        EpicTask firstEpicTaskUpdated = new EpicTask(firstEpic.getId(), "First epic updated",
                "FirstEpicTask description updated");
        firstEpicTaskUpdated.addSubTaskId(firstSubTaskUpdated.getId());
        firstEpicTaskUpdated.addSubTaskId(secondSubTaskUpdated.getId());
        testManager.updateEpicTask(firstEpicTaskUpdated);
        System.out.println("Epics: " + testManager.getAllEpics());

        System.out.println();
        System.out.println("Check removeTaskById method");
        testManager.removeTaskById(firstTaskUpdated.getId());
        System.out.println("Tasks: " + testManager.getAllTasks());

        System.out.println();
        System.out.println("Check removeSubTaskById method");
        testManager.removeSubTaskById(firstSubTaskUpdated.getId());
        System.out.println("SubTasks: " + testManager.getAllSubTasks());
        System.out.println("Epics: " + testManager.getAllEpics());

        System.out.println();
        System.out.println("Check removeEpicById method");
        testManager.removeEpicTaskById(firstEpic.getId());
        System.out.println("SubTasks: " + testManager.getAllSubTasks());
        System.out.println("Epics: " + testManager.getAllEpics());

        System.out.println();
        System.out.println("Check clearTaskList method");
        testManager.clearTaskList();
        System.out.println("Tasks: " + testManager.getAllTasks());

        System.out.println();
        System.out.println("Check clearSubTaskList method");
        testManager.clearSubTaskLists();
        System.out.println("SubTasks: " + testManager.getAllSubTasks());
        System.out.println("Epics: " + testManager.getAllEpics());

        System.out.println();
        System.out.println("Check clearEpicTaskList method");
        testManager.clearEpicTaskLists();
        System.out.println("SubTasks: " + testManager.getAllSubTasks());
        System.out.println("Epics: " + testManager.getAllEpics());
    }
}
