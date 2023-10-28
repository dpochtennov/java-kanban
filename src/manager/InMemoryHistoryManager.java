package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        history.addLast(task);
        if (history.size() > 10) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
