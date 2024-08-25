import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    public void subtaskCannotBeMadeEpicForHimself() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Добавляем эпик и подзадачу
        final Epic epic = new Epic("Эпик 1", "Описание 1");
        final int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask1 = new Subtask("Сабтаск 1", "описание 2", Status.NEW, epicId);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        // Пытаемя добавить Сабтаск с сабтаском в качестве эпика
        final Subtask subtask2 = new Subtask("Сабтаск 2", "Описание 3", Status.IN_PROGRESS, subtask1Id);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        assert (subtask2Id == -1);
    }

    @Test
    public void getTaskById() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task = new Task("Задача 1", "Описание 1", Status.DONE);

        final int taskId = taskManager.addNewTask(task);

        assertNotNull(taskManager.getTask(taskId));
    }

    @Test
    public void getEpicById() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Epic task = new Epic("Задача 1", "Описание 1");

        final int epicId = taskManager.addNewEpic(task);

        assertNotNull(taskManager.getEpic(epicId));
    }

    @Test
    public void getSubtaskById() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Epic task = new Epic("Задача 1", "Описание 1");

        final int epicId = taskManager.addNewEpic(task);

        final Subtask subtask = new Subtask("Сабтаск 1", "Описание 2", Status.NEW, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        assertNotNull(taskManager.getSubtask(subtaskId));
    }

    @Test
    public void tasksWithSpecifiedIdAndGeneratedNotConflict() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        final Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);

        final int task1Id = taskManager.addNewTask(task1);
        task2.setId(task1Id);

        final int task2Id = taskManager.addNewTask(task2);

        assertNotEquals(task1Id, task2Id);
    }

    @Test
    public void taskNotChangeWhenAdd() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        final int task1Id = taskManager.addNewTask(task1);

        assertEquals(task1.getName(), "Задача 1");
        assertEquals(task1.getDescription(), "Описание 1");
        assertEquals(task1.getStatus(), Status.NEW);
    }
}