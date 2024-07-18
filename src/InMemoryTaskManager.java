import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();

    private final HistoryManager history = Managers.getDefaultHistory();

    private int taskCounter = 0;

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtask(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }

        Epic epic = epics.get(epicId);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (!subtasks.containsKey(subtaskId)) {
                continue;
            }

            epicSubtasks.add(subtasks.get(subtaskId));
        }

        return epicSubtasks;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        final Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }

        epic.deleteSubtasks();

        updateEpicStatus(epicId);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            // обновление статуса эпиков внутри
            deleteEpicSubtasks(epic.getId());
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();

        epics.clear();
    }

    @Override
    public Epic getEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }

        Epic epic = epics.get(epicId);
        history.add((Task) epic);

        return epic;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return null;
        }

        Subtask subtask = subtasks.get(subtaskId);
        history.add((Task) subtask);
        return subtask;
    }

    @Override
    public Task getTask(int taskId) {
        if (!tasks.containsKey(taskId)) {
            return null;
        }

        Task task = tasks.get(taskId);
        history.add(task);

        return task;
    }

    @Override
    public int addNewTask(Task task) {
        if (task == null) {
            return -1;
        }

        final int newTaskId = taskCounter++;
        task.setId(newTaskId);
        tasks.put(newTaskId, task);

        return newTaskId;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return -1;
        }

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

    @Override
    public int addNewEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }

        final int newEpicId = taskCounter++;
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);

        return newEpicId;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }

        tasks.replace(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId()) || !epics.containsKey(subtask.getEpicId())) {
            return;
        }

        subtasks.replace(subtask.getId(), subtask);

        // Обновление статуса эпика
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }

        epics.replace(epic.getId(), epic);
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }

        final Subtask subtask = subtasks.get(subtaskId);
        if (epics.containsKey(subtask.getEpicId())) {
            final Epic epic = epics.get(subtask.getEpicId());
            epic.deleteSubtask(subtaskId);
            updateEpicStatus(epic.getId());
        }

        subtasks.remove(subtaskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        deleteEpicSubtasks(epicId);

        epics.remove(epicId);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    public void updateEpicStatus(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

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
