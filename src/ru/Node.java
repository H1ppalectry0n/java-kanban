package ru;

import ru.tasks.Task;

// Узел двусвязного списка
public class Node {

    public Task task;
    public Node next;
    public Node prev;

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}
