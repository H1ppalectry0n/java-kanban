package ru;

import ru.tasks.Epic;
import ru.tasks.Subtask;
import ru.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Epic> getEpics();

    List<Subtask> getEpicSubtask(int epicId);

    List<Subtask> getSubtasks();

    List<Task> getTasks();

    void deleteAllTasks();

    void deleteEpicSubtasks(int epicId);

    void deleteAllSubtasks();

    void deleteAllEpics();

    Epic getEpic(int epicId);

    Subtask getSubtask(int subtaskId);

    Task getTask(int taskId);

    int addNewTask(Task task);

    int addNewSubtask(Subtask subtask);

    int addNewEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int taskId);

    void deleteSubtask(int subtaskId);

    void deleteEpic(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}