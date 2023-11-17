package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.util.Collections.reverse;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<UUID, HistoryNode<Task>> idsToNodeMapper;
    private HistoryNode<Task> head;

    public InMemoryHistoryManager() {
        idsToNodeMapper = new HashMap<>();
        head = null;
    }

    @Override
    public void add(Task task) {
        UUID id = task.getId();
        if (idsToNodeMapper.containsKey(id)) {
            remove(id);
        }
        HistoryNode<Task> node = new HistoryNode<>(task);
        if (!Objects.isNull(head)) {
            node.setPrev(head);
            head.setNext(node);
        }
        head = node;
        idsToNodeMapper.put(id, node);
    }

    @Override
    public void remove(UUID id) {
        HistoryNode<Task> nodeToDelete = idsToNodeMapper.get(id);
        removeNode(nodeToDelete);
        idsToNodeMapper.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        HistoryNode<Task> currentNode = head;

        while (!Objects.isNull(currentNode)) {
            result.add(currentNode.getData());
            HistoryNode<Task> previousNode = currentNode.getPrev();
            currentNode = previousNode;
        }
        reverse(result);
        return result;
    }

    private void removeNode(HistoryNode<Task> nodeToRemove) {
        if (Objects.isNull(nodeToRemove)) {
            return;
        }
        HistoryNode<Task> prevNode = nodeToRemove.getPrev();
        HistoryNode<Task> nextNode = nodeToRemove.getNext();

        if (!Objects.isNull(prevNode)) {
            prevNode.setNext(nextNode);
        }

        if (!Objects.isNull(nextNode)) {
            nextNode.setPrev(prevNode);
        } else {
            head = prevNode;
        }

        nodeToRemove.setNext(null);
        nodeToRemove.setPrev(null);
    }
}

class HistoryNode<T> {
    private final T data;
    private HistoryNode<T> next;
    private HistoryNode<T> prev;

    public HistoryNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public HistoryNode<T> getNext() {
        return next;
    }

    public void setNext(HistoryNode<T> historyNode) {
        this.next = historyNode;
    }

    public HistoryNode<T> getPrev() {
        return prev;
    }

    public void setPrev(HistoryNode<T> historyNode) {
        this.prev = historyNode;
    }

    public T getData() {
        return data;
    }
}
