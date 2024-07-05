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

    private int taskCounter = 0;

    ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    ArrayList<Subtask> getEpicSubtask(int epicId) {
        if (!epics.containsKey(epicId)) return null;

        Epic epic = epics.get(epicId);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (!subtasks.containsKey(subtaskId)) continue;

            epicSubtasks.add(subtasks.get(subtaskId));
        }

        return epicSubtasks;
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

        epic.deleteSubtasks();

        updateEpicStatus(epicId);
    }

    void deleteAllSubtasks() {
        subtasks.clear();
        taskCounter = 0;

        for (Epic epic : epics.values()) {
            // обновление статуса эпиков внутри
            deleteEpicSubtasks(epic.getId());
        }
    }

    void deleteAllEpics() {
        deleteAllSubtasks();

        epics.clear();
        taskCounter = 0;
    }

    Epic getEpic(int epicId) {
        if (!epics.containsKey(epicId)) return null;

        return epics.get(epicId);
    }

    Subtask getSubtask(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) return null;

        return subtasks.get(subtaskId);
    }

    Task getTask(int taskId) {
        if (!tasks.containsKey(taskId)) return null;

        return tasks.get(taskId);
    }

    int addNewTask(Task task) {
        if (task == null) return -1;

        final int newTaskId = taskCounter++;
        task.setId(newTaskId);
        tasks.put(newTaskId, task);

        return newTaskId;
    }

    int addNewSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) return -1;

        // Добавление Subtask в коллекцию
        final int newSubtaskId = taskCounter++;
        subtask.setId(newSubtaskId);
        subtasks.put(newSubtaskId, subtask);

        // Обновление статуса эпика
        Epic epic = epics.get(subtask.getEpicId());
        epic.addNewSubtaskId(newSubtaskId);
        updateEpicStatus(subtask.getEpicId());

        return newSubtaskId;
    }

    int addNewEpic(Epic epic) {
        if (epic == null) return -1;

        final int newEpicId = taskCounter++;
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);

        return newEpicId;
    }

    void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) return;

        tasks.replace(task.getId(), task);
    }

    void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId()) || !epics.containsKey(subtask.getEpicId()))
            return;

        subtasks.replace(subtask.getId(), subtask);

        // Обновление статуса эпика
        updateEpicStatus(subtask.getEpicId());
    }

    void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;

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
            updateEpicStatus(epic.getId());
        }

        subtasks.remove(subtaskId);
    }

    void deleteEpic(int epicId) {
        deleteEpicSubtasks(epicId);

        epics.remove(epicId);
    }

    public void updateEpicStatus(int epicId) {
        if (!epics.containsKey(epicId)) return;

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
