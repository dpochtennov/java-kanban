import tasks.*;

import java.util.ArrayList;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        TaskManager testManager = new TaskManager();

        System.out.println("Check that task is added");
        Task firstTask = testManager.addTask("First task", "Some first task", TaskStatus.NEW);
        System.out.println(testManager.getAllTasks());
        System.out.println();

        System.out.println("Check that task is updated");
        testManager.updateTask(firstTask.getId(), "First task", "Updated one",
                TaskStatus.IN_PROGRESS);
        System.out.println(testManager.getAllTasks());
        System.out.println();

        System.out.println("Check that epic is added");
        EpicTask firstEpic = testManager.addEpicTask("First epic",
                "Some first Epic", TaskStatus.NEW, new ArrayList<>());
        System.out.println(testManager.getAllEpics());
        System.out.println();


        System.out.println("Check that epic is updated");
        EpicTask updatedEpic = testManager.updateEpicTask(firstEpic.getId(), "First updated epic",
                "Some first updated Epic", TaskStatus.IN_PROGRESS, new ArrayList<>());
        System.out.println(testManager.getAllEpics());
        System.out.println();

        System.out.println("Check that subtask is added, check getSubtasksOfEpic, check getAnyTaskById");
        UUID updatedEpicId = updatedEpic.getId();
        SubTask firstSubTask = testManager.addSubTask("First subtask", "FirstSubTask description",
                TaskStatus.NEW, updatedEpicId);
        System.out.println(testManager.getAllEpics());
        System.out.println(testManager.getSubtasksOfEpic(updatedEpicId));
        System.out.println(testManager.getAnyTaskById(firstSubTask.getId()));
        System.out.println();

        System.out.println("Check that subtask is updated and epic status is recalculated");
        SubTask updatedSubTask = testManager.updateSubTask(firstSubTask.getId(),
                "Updated one", "description", TaskStatus.IN_PROGRESS, updatedEpicId);
        System.out.println(testManager.getAnyTaskById(updatedEpicId));
        System.out.println(testManager.getAnyTaskById(updatedSubTask.getId()));
        System.out.println();

        System.out.println("Additional check for epic status recalculation");
        EpicTask newEpic = testManager.addEpicTask("New epic", "new Epic description",
                TaskStatus.NEW, new ArrayList<>());
        SubTask thirdSubTask = testManager.addSubTask("third one", "description",
                TaskStatus.DONE, newEpic.getId());
        newEpic.addSubTaskId(thirdSubTask.getId());
        System.out.println(testManager.getAnyTaskById(newEpic.getId()));
        System.out.println();

        System.out.println("Check removal logic");
        System.out.println("Before epic deletion: " + testManager.getAllSubTasks());
        testManager.removeTaskById(newEpic.getId());
        System.out.println(testManager.getAllEpics());
        System.out.println("After epic deletion: " + testManager.getAllSubTasks());
        System.out.println();

        System.out.println("Check clear logic");
        testManager.clearTaskLists();
        System.out.println(testManager.getAllTasks());
        System.out.println(testManager.getAllEpics());
        System.out.println();
    }
}
