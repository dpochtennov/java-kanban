package manager.historyManager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<UUID, HistoryNode<Task>> idsToNodeMapper;
    private HistoryNode<Task> head;
    private HistoryNode<Task> tail;

    public InMemoryHistoryManager() {
        idsToNodeMapper = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public void add(Task task) {
        UUID id = task.getId();
        if (idsToNodeMapper.containsKey(id)) {
            remove(id);
        }
        HistoryNode<Task> node = new HistoryNode<>(task);
        if (Objects.isNull(tail)) {
            tail = node;
        }
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
        HistoryNode<Task> currentNode = tail;

        while (!Objects.isNull(currentNode)) {
            result.add(currentNode.getData());
            HistoryNode<Task> nextNode = currentNode.getNext();
            currentNode = nextNode;
        }
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
        } else {
            tail = nextNode;
        }

        if (!Objects.isNull(nextNode)) {
            nextNode.setPrev(prevNode);
        } else {
            head = prevNode;
        }

        nodeToRemove.setNext(null);
        nodeToRemove.setPrev(null);
    }

    private static class HistoryNode<T> {
        private final T data;
        private HistoryNode<T> next;
        private HistoryNode<T> prev;

        HistoryNode(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

        HistoryNode<T> getNext() {
            return next;
        }

        void setNext(HistoryNode<T> historyNode) {
            this.next = historyNode;
        }

        HistoryNode<T> getPrev() {
            return prev;
        }

        void setPrev(HistoryNode<T> historyNode) {
            this.prev = historyNode;
        }

        T getData() {
            return data;
        }
    }
}
