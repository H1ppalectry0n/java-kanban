import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node head = null;
    private Node tail = null;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        remove(task.getId());
        linkLast(task);
    }

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node node = new Node(oldTail, new Task(task), null);
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
        Node currentNode = head;
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

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }

        final Node next = node.next;
        final Node prev = node.prev;

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

