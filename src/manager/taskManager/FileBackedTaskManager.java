package manager.taskManager;

import customExceptions.ManagerReadException;
import customExceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String COMMA_DELIMITER = ",";
    private static final int ID_POSITION = 0;
    private static final int NAME_POSITION = 2;
    private static final int STATUS_POSITION = 3;
    private static final int DESCRIPTION_POSITION = 4;
    private static final int EPIC_ID_POSITION = 5;

    private final File tasksFile;

    private FileBackedTaskManager(File tasksFile) {
        this.tasksFile = tasksFile;
    }

    /**
     * This static method reads content of csv file where tasks are stored and returns TaskManager
     * which works with that file.
     *
     * @param file csv file where tasks are stored.
     * @return FileBackedTaskManager TaskManager which works with csv file.
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<String> records = readFile(file);
        List<Task> tasks = serializeTasks(records);
        for (Task task : tasks) {
            if (task instanceof EpicTask) {
                manager.addEpicTask((EpicTask) task);
            } else if (task instanceof SubTask) {
                manager.addSubTask((SubTask) task);
            } else {
                manager.addTask(task);
            }
        }
        List<UUID> history = getHistory(records);
        for (UUID id : history) {
            manager.getAnyTaskById(id);
        }
        return manager;
    }

    public static void main(String[] args) {
        File tasks = new File("src/files/tasks.csv");

        System.out.println("Check that tasks from file are read correctly (and saved also)\n");
        TaskManager manager = FileBackedTaskManager.loadFromFile(tasks);
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getHistory());

        System.out.println("Check that file is updated when we add some tasks via second manager\n");
        Task newTask = new Task("New task", "Some new task", TaskStatus.NEW);
        manager.addTask(newTask);
        EpicTask newEpic = new EpicTask("New epic", "Some new Epic");
        manager.addEpicTask(newEpic);
        SubTask newSubTask = new SubTask("New subtask", "NewSubTask description", TaskStatus.NEW, newEpic.getId());
        manager.addSubTask(newSubTask);
        manager.getTaskById(newTask.getId());
        TaskManager secondManager = FileBackedTaskManager.loadFromFile(tasks);
        System.out.println(secondManager.getAllTasks());
        System.out.println(secondManager.getAllEpics());
        System.out.println(secondManager.getAllSubTasks());
        System.out.println(secondManager.getHistory());
        System.out.println(manager.getAllTasks().toString().equals(secondManager.getAllTasks().toString()));
        System.out.println(manager.getAllEpics().toString().equals(secondManager.getAllEpics().toString()));
        System.out.println(manager.getAllSubTasks().toString().equals(secondManager.getAllSubTasks().toString()));
        manager.removeTaskById(newTask.getId());
        manager.removeSubTaskById(newSubTask.getId());
        manager.removeEpicTaskById(newEpic.getId());
    }

    private static List<String> readFile(File file) {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException exception) {
            throw new ManagerReadException("Cannot read file: " + exception.getMessage());
        }
        return records;
    }

    private static List<Task> serializeTasks(List<String> records) {
        List<Task> result = new ArrayList<>();
        for (String record : records) {
            if (record.contains(COMMA_DELIMITER + TaskTypes.SUBTASK + COMMA_DELIMITER)) {
                Task subTask = getTaskFromString(record, TaskTypes.SUBTASK);
                result.add(subTask);
            }
            if (record.contains(COMMA_DELIMITER + TaskTypes.EPIC + COMMA_DELIMITER)) {
                Task epic = getTaskFromString(record, TaskTypes.EPIC);
                result.add(epic);
            }
            if (record.contains(COMMA_DELIMITER + TaskTypes.TASK + COMMA_DELIMITER)) {
                Task task = getTaskFromString(record, TaskTypes.TASK);
                result.add(task);
            }
        }
        return result;
    }

    private static List<UUID> getHistory(List<String> records) {
        List<UUID> history = new ArrayList<>();
        if (!records.isEmpty()) {
            String lastRow = records.get(records.size() - 1);
            history = getUUIDsFromString(lastRow);
        }
        return history;
    }

    private static List<UUID> getUUIDsFromString(String value) {
        return List.of(value.split(COMMA_DELIMITER)).stream()
                .map(str -> UUID.fromString(str))
                .collect(toList());
    }

    private static Task getTaskFromString(String line, TaskTypes type) {
        String[] values = line.split(COMMA_DELIMITER);
        UUID id = UUID.fromString(values[ID_POSITION]);
        String name = values[NAME_POSITION];
        String description = values[DESCRIPTION_POSITION];
        TaskStatus status = TaskStatus.valueOf(values[STATUS_POSITION]);
        switch (type) {
            case SUBTASK: {
                UUID epicId = UUID.fromString(values[EPIC_ID_POSITION]);
                return new SubTask(id, name, description, status, epicId);
            }
            case EPIC: {
                List<UUID> subTaskIds = new ArrayList<>();
                return new EpicTask(id, name, description, status, subTaskIds);
            }
            default: {
                return new Task(id, name, description, status);
            }
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void removeTaskById(UUID id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public Task getTaskById(UUID id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public void removeSubTaskById(UUID id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void clearSubTaskLists() {
        super.clearSubTaskLists();
        save();
    }

    @Override
    public SubTask getSubTaskById(UUID id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public EpicTask addEpicTask(EpicTask epic) {
        super.addEpicTask(epic);
        save();
        return epic;
    }

    @Override
    public EpicTask updateEpicTask(EpicTask epic) {
        super.updateEpicTask(epic);
        save();
        return epic;
    }

    @Override
    public void removeEpicTaskById(UUID id) {
        super.removeEpicTaskById(id);
        save();
    }

    @Override
    public void clearEpicTaskLists() {
        super.clearEpicTaskLists();
        save();
    }

    @Override
    public EpicTask getEpicTaskById(UUID id) {
        EpicTask epic = super.getEpicTaskById(id);
        save();
        return epic;
    }

    private void save() {
        List<Task> tasks = super.getAllTasks();
        List<EpicTask> epics = super.getAllEpics();
        List<SubTask> subTasks = super.getAllSubTasks();
        try (FileWriter fw = new FileWriter(tasksFile, false)) {
            for (Task task : tasks) {
                fw.write(task.toString() + "\n");
            }
            for (Task epic : epics) {
                fw.write(epic.toString() + "\n");
            }
            for (Task subTask : subTasks) {
                fw.write(subTask.toString() + "\n");
            }
            fw.write("\n");
            String history = historyToString();
            fw.write(history);
        } catch (IOException exception) {
            throw new ManagerSaveException("Cannot save to file: " + exception.getMessage());
        }
    }

    private String historyToString() {
        List<Task> history = super.getHistory();
        return history.stream().map(task -> task.getId().toString()).collect(Collectors.joining(COMMA_DELIMITER));
    }
}