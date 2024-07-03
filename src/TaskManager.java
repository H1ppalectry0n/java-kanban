import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();

    private int epicCounter = 0;
    private int subtaskCounter = 0;
    private int taskCounter = 0;

    ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    ArrayList<Subtask> getEpicSubtask(int epicId) {
        assert (epics.containsKey(epicId));

        Epic epic = epics.get(epicId);
        ArrayList<Subtask> _subtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            assert (subtasks.containsKey(subtaskId));

            _subtasks.add(subtasks.get(subtaskId));
        }

        return _subtasks;
    }

    ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    void deleteAllTasks() {
        tasks.clear();
        taskCounter = 0;
    }

    void deleteEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) return;

        final Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }

        epic.setStatus(Status.NEW);
    }

    void deleteAllSubtasks() {
        subtasks.clear();
        subtaskCounter = 0;

        for (Epic epic : epics.values()) {
            deleteEpicSubtasks(epic.getId());
        }
    }

    void deleteAllEpics() {
        deleteAllSubtasks();

        epics.clear();
        epicCounter = 0;
    }

    Epic getEpic(int epicId) {
        assert (epics.containsKey(epicId));

        return epics.get(epicId);
    }

    Subtask getSubtask(int subtaskId) {
        assert (subtasks.containsKey(subtaskId));

        return subtasks.get(subtaskId);
    }

    Task getTask(int taskId) {
        assert (tasks.containsKey(taskId));

        return tasks.get(taskId);
    }

    int addNewTask(Task task) {
        assert (task != null);

        final int newTaskId = taskCounter++;
        task.setId(newTaskId);
        tasks.put(newTaskId, task);

        return newTaskId;
    }

    int addNewSubtask(Subtask subtask) {
        assert (subtask != null);
        assert (epics.containsKey(subtask.getEpicId()));

        // Добавление Subtask в коллекцию
        final int newSubtaskId = subtaskCounter++;
        subtask.setId(newSubtaskId);
        subtasks.put(newSubtaskId, subtask);

        // Обновление статуса эпика
        Epic epic = epics.get(subtask.getEpicId());
        epic.addNewSubtaskId(newSubtaskId);
        updateEpicStatus(subtask.getEpicId());

        return newSubtaskId;
    }

    int addNewEpic(Epic epic) {
        assert (epic != null);

        final int newEpicId = epicCounter++;
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);

        return newEpicId;
    }

    void updateTask(Task task) {
        assert (task != null);
        assert (tasks.containsKey(task.getId()));

        tasks.replace(task.getId(), task);
    }

    void updateSubtask(Subtask subtask) {
        assert (subtask != null);
        assert (subtasks.containsKey(subtask.getId()));
        assert (epics.containsKey(subtask.getEpicId()));

        subtasks.replace(subtask.getId(), subtask);

        // Обновление статуса эпика
        updateEpicStatus(subtask.getEpicId());
    }

    void updateEpic(Epic epic) {
        assert (epic != null);
        assert (epics.containsKey(epic.getId()));

        epics.replace(epic.getId(), epic);
    }

    void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    void deleteSubtask(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) return;

        final Subtask subtask = subtasks.get(subtaskId);
        if (epics.containsKey(subtask.getEpicId())) {
            final Epic epic = epics.get(subtask.getEpicId());
            epic.deleteSubtask(subtaskId);
        }

        subtasks.remove(subtaskId);
    }

    void deleteEpic(int epicId) {
        deleteEpicSubtasks(epicId);

        epics.remove(epicId);
    }

    public void updateEpicStatus(int epicId) {
        assert (epics.containsKey(epicId));

        boolean allIsNew = true;
        boolean allIsDone = true;

        for (Subtask subtask : getEpicSubtask(epicId)) {
            if (subtask.getStatus() != Status.NEW) {
                allIsNew = false;
            }

            if (subtask.getStatus() != Status.DONE) {
                allIsDone = false;
            }
        }

        Epic epic = epics.get(epicId);
        // Первая проверка на NEW т.к. если подзадач нет то оба флага остануться true
        if (allIsNew) {
            epic.setStatus(Status.NEW);
        } else if (allIsDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
