import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getEpicSubtask(int epicId);

    ArrayList<Subtask> getSubtasks();

    ArrayList<Task> getTasks();

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

    ArrayList<Task> getHistory();
}
