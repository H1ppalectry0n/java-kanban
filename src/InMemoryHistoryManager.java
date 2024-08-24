import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    final private HashMap<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        remove(task.getId());
        linkLast(task);
    }

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> node = new Node<>(oldTail, new Task(task), null);
        tail = node;
        history.put(task.getId(), node);
        if (oldTail == null)
            head = node;
        else
            oldTail.next = node;
    }


    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (!(currentNode == null)) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }


    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        final Node<Task> next = node.next;
        final Node<Task> prev = node.prev;

        if (next != null) {
            next.prev = prev;
        }

        if (prev != null) {
            prev.next = next;
        }

        if (node == head) {
            head = next;
        }

        if (node == tail) {
            tail = prev;
        }
    }

}

// Узел двусвязного списка
class Node<Task> {

    public Task task;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Node<Task> prev, Task task, Node<Task> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}