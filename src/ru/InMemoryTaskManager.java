package ru;

import ru.tasks.Epic;
import ru.tasks.Status;
import ru.tasks.Subtask;
import ru.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Task> tasks = new HashMap<>();

    // Проверка наличия времени начала до попадания в список
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(t -> t.getStartTime().get()));

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
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        final Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            deleteSubtask(subtaskId);
        }

        epic.deleteSubtasks();

        updateEpicStatus(epicId);
        updateEpicDuration(epicId);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
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

        if (task.getStartTime().isPresent()) {
            if (checkTasksOverlapping(task)) {
                throw new ManagerOverlapException("Время задачи уже занято");
            }

            prioritizedTasks.add(task);
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

        if (subtask.getStartTime().isPresent()) {
            if (checkTasksOverlapping(subtask)) {
                throw new ManagerOverlapException("Время задачи уже занято");
            }

            prioritizedTasks.add(subtask);
        }

        // Добавление Subtask в коллекцию
        final int newSubtaskId = taskCounter++;
        subtask.setId(newSubtaskId);
        subtasks.put(newSubtaskId, subtask);

        // Обновление статуса эпика
        Epic epic = epics.get(subtask.getEpicId());
        epic.addNewSubtaskId(newSubtaskId);
        updateEpicStatus(subtask.getEpicId());
        updateEpicDuration(subtask.getEpicId());

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

        if (task.getStartTime().isPresent()) {
            prioritizedTasks.remove(task);

            if (checkTasksOverlapping(task)) {
                throw new ManagerOverlapException("Время задачи уже занято");
            }

            prioritizedTasks.add(task);
        }

        tasks.replace(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId()) || !epics.containsKey(subtask.getEpicId())) {
            return;
        }

        if (subtask.getStartTime().isPresent()) {
            prioritizedTasks.remove(subtask);

            if (checkTasksOverlapping(subtask)) {
                throw new ManagerOverlapException("Время задачи уже занято");
            }

            prioritizedTasks.add(subtask);
        }

        subtasks.replace(subtask.getId(), subtask);

        // Обновление статуса эпика
        updateEpicStatus(subtask.getEpicId());
        updateEpicDuration(subtask.getEpicId());
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
        Task task = tasks.get(taskId);
        if (task != null) {
            prioritizedTasks.remove(task);
            tasks.remove(taskId);
        }
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }

        final Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            return;
        }

        prioritizedTasks.remove(subtask);
        subtasks.remove(subtaskId);

        if (epics.containsKey(subtask.getEpicId())) {
            final Epic epic = epics.get(subtask.getEpicId());
            epic.deleteSubtask(subtaskId);
            updateEpicStatus(epic.getId());
            updateEpicDuration(epic.getId());
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        deleteEpicSubtasks(epicId);

        epics.remove(epicId);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) history.getHistory();
    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<Task>(prioritizedTasks);
    }

    public boolean checkTasksOverlapping(Task task) throws ManagerOverlapException {
        if (task.getStartTime().isEmpty()) {
            throw new ManagerOverlapException("Время начала задачи не заданно " + task);
        }

        final LocalDateTime taskStartTime = task.getStartTime().get();
        final LocalDateTime taskEndTime = task.getEndTime().get();

        Optional<Task> overlappingTask = getPrioritizedTasks().stream()
                .filter(t -> {
                    final LocalDateTime startTime = t.getStartTime().get();
                    final LocalDateTime stopTime = t.getEndTime().get();

                    // проверка на пересечение границ отрезков
                    return (startTime.isBefore(taskEndTime) && !startTime.isBefore(taskStartTime))
                            || (taskStartTime.isBefore(stopTime) && !taskStartTime.isBefore(startTime));
                }).findFirst();

        return overlappingTask.isPresent();
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

    public void updateEpicDuration(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        List<Subtask> sortedSubtasks = getEpicSubtask(epicId).stream()
                .filter(s -> s.getStartTime().isPresent())
                .sorted(Comparator.comparing(t -> t.getStartTime().get())).toList();

        Epic epic = epics.get(epicId);

        if (sortedSubtasks.isEmpty()) {
            epic.setStartTime(null);
        } else {
            final LocalDateTime startTime = sortedSubtasks.getFirst().getStartTime().get();
            final LocalDateTime lastSubtaskStartTime = sortedSubtasks.getLast().getStartTime().get();
            final Duration duration = Duration.between(startTime, lastSubtaskStartTime).plus(sortedSubtasks.getLast().getDuration());

            epic.setStartTime(startTime);
            epic.setDuration(duration);
        }
    }
}
