package ru;

import ru.tasks.Epic;
import ru.tasks.Subtask;
import ru.tasks.Task;
import ru.tasks.TaskUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            if (!br.ready()) {
                return manager;
            }

            // Чтение заголовка
            br.readLine();

            List<Task> tasks = new ArrayList<>();
            List<Epic> epics = new ArrayList<>();
            List<Subtask> subtasks = new ArrayList<>();

            while (br.ready()) {
                Task task = TaskUtil.fromString(br.readLine());
                switch (task.getType()) {
                    case EPIC -> {
                        epics.add((Epic) task);
                        break;
                    }
                    case SUBTASK -> {
                        subtasks.add((Subtask) task);
                        break;
                    }
                    case TASK -> {
                        tasks.add(task);
                        break;
                    }
                }
            }

            manager.setTasks(tasks);
            manager.setEpics(epics);
            manager.setSubtasks(subtasks);

            return manager;
        }
    }

    private void setEpics(List<Epic> newEpics) {
        for (Epic epic : newEpics) {
            this.epics.put(epic.getId(), epic);
        }
    }

    private void setSubtasks(List<Subtask> newSubtasks) {
        for (Subtask subtask : newSubtasks) {
            this.subtasks.put(subtask.getId(), subtask);
        }
    }

    private void setTasks(List<Task> newTasks) {
        for (Task task : newTasks) {
            this.tasks.put(task.getId(), task);
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : tasks.values()) {
                fileWriter.write(task + "\n");
            }

            for (Epic epic : epics.values()) {
                fileWriter.write(epic + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(subtask + "\n");
            }

        } catch (IOException | NullPointerException e) {
            throw new ManagerSaveException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteEpicSubtasks(int epicId) {
        super.deleteEpicSubtasks(epicId);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();

        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();

        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();

        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }
}
