import org.junit.jupiter.api.Test;
import ru.InMemoryTaskManager;
import ru.ManagerOverlapException;
import ru.tasks.Epic;
import ru.tasks.Status;
import ru.tasks.Subtask;
import ru.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    @Test
    public void subtaskCannotBeMadeEpicForHimself() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Добавляем эпик и подзадачу
        final Epic epic = new Epic("Эпик 1", "Описание 1");
        final int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask1 = new Subtask("Сабтаск 1", "описание 2", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ZERO, epicId);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        // Пытаемя добавить Сабтаск с сабтаском в качестве эпика
        final Subtask subtask2 = new Subtask("Сабтаск 2", "Описание 3", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(2), Duration.ZERO, subtask1Id);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        assert (subtask2Id == -1);
    }

    @Test
    public void getTaskById() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task = new Task("Задача 1", "Описание 1", Status.DONE, LocalDateTime.now().plusMinutes(3), Duration.ZERO);

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

        final Subtask subtask = new Subtask("Сабтаск 1", "Описание 2", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ZERO, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        assertNotNull(taskManager.getSubtask(subtaskId));
    }

    @Test
    public void tasksWithSpecifiedIdAndGeneratedNotConflict() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ZERO);
        final Task task2 = new Task("Задача 2", "Описание 2", Status.NEW, LocalDateTime.now().plusMinutes(2), Duration.ZERO);

        final int task1Id = taskManager.addNewTask(task1);
        task2.setId(task1Id);

        final int task2Id = taskManager.addNewTask(task2);

        assertNotEquals(task1Id, task2Id);
    }

    @Test
    public void taskNotChangeWhenAdd() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        final int task1Id = taskManager.addNewTask(task1);

        assertEquals(task1.getName(), "Задача 1");
        assertEquals(task1.getDescription(), "Описание 1");
        assertEquals(task1.getStatus(), Status.NEW);
    }

    @Test
    public void overlappingTasks1() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        final Task task2 = new Task("Задача 2", "Описание 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        taskManager.addNewTask(task1);


        assertThrows(ManagerOverlapException.class, () -> taskManager.addNewTask(task2));
    }

    @Test
    public void overlappingTasks2() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        final Task task2 = new Task("Задача 2", "Описание 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        taskManager.addNewTask(task1);


        assertThrows(ManagerOverlapException.class, () -> taskManager.addNewTask(task2));
    }

    @Test
    public void sideBySideTasksNotOverlapping() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.of(2024, 2, 1, 11, 0), Duration.ofMinutes(30));
        final Task task2 = new Task("Задача 2", "Описание 2", Status.NEW, LocalDateTime.of(2024, 2, 1, 11, 0).plusMinutes(30), Duration.ofMinutes(30));

        taskManager.addNewTask(task1);

        assertDoesNotThrow(() -> taskManager.addNewTask(task2));
    }

    @Test
    public void prioritizedTask() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        final Task task1 = new Task("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 11, 0), Duration.ofMinutes(30));
        final Task task2 = new Task("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 11, 0).plusMinutes(30), Duration.ofMinutes(30));
        final Task task3 = new Task("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 11, 0).plusMinutes(60), Duration.ofMinutes(30));
        final Task task4 = new Task("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 11, 0).plusMinutes(90), Duration.ofMinutes(30));
        final Task task5 = new Task("Задача", "Описание", Status.NEW, null, Duration.ofMinutes(30));
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task5);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task4);

        final Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask1 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0), Duration.ofMinutes(30), epicId);
        final Subtask subtask2 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(30), Duration.ofMinutes(30), epicId);
        final Subtask subtask3 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(60), Duration.ofMinutes(30), epicId);
        final Subtask subtask4 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(90), Duration.ofMinutes(30), epicId);
        final Subtask subtask5 = new Subtask("Задача", "Описание", Status.NEW, null, Duration.ofMinutes(30), epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask3);
        taskManager.addNewSubtask(subtask5);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask4);


        assertEquals(taskManager.getPrioritizedTasks().size(), 8);
        assertEquals(taskManager.getPrioritizedTasks().getFirst(), task1);
        assertEquals(taskManager.getPrioritizedTasks().getLast(), subtask4);
    }

    @Test
    public void epicStartTimeAndDurationUpdatingAddNewSubtasks() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        final Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask1 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0), Duration.ofMinutes(30), epicId);
        final Subtask subtask2 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(30), Duration.ofMinutes(30), epicId);
        final Subtask subtask3 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(60), Duration.ofMinutes(30), epicId);
        final Subtask subtask4 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(90), Duration.ofMinutes(30), epicId);
        final Subtask subtask5 = new Subtask("Задача", "Описание", Status.NEW, null, Duration.ofMinutes(30), epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask3);
        taskManager.addNewSubtask(subtask5);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask4);

        final LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 15, 0);
        final LocalDateTime endTime = LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(120);

        assertEquals(epic.getStartTime().get(), startTime);
        assertEquals(epic.getEndTime().get(), endTime);
    }

    @Test
    public void epicStartTimeAndDurationUpdatingUpdateSubtasks() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        final Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask1 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0), Duration.ofMinutes(30), epicId);
        final Subtask subtask2 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(30), Duration.ofMinutes(30), epicId);
        final Subtask subtask3 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(60), Duration.ofMinutes(30), epicId);
        final Subtask subtask4 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(90), Duration.ofMinutes(30), epicId);
        final Subtask subtask5 = new Subtask("Задача", "Описание", Status.NEW, null, Duration.ofMinutes(30), epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask3);
        taskManager.addNewSubtask(subtask5);
        int subtaskId = taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask4);

        final Subtask updatedSubtask = taskManager.getSubtask(subtaskId);
        updatedSubtask.setStartTime(LocalDateTime.of(2024, 2, 3, 15, 0));
        updatedSubtask.setDuration(Duration.ofDays(2));
        taskManager.updateSubtask(updatedSubtask);

        final LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 15, 0);
        final LocalDateTime endTime = LocalDateTime.of(2024, 2, 3, 15, 0).plusDays(2);

        assertEquals(epic.getStartTime().get(), startTime);
        assertEquals(epic.getEndTime().get(), endTime);
    }

    @Test
    public void epicStartTimeAndDurationUpdatingRemoveSubtasks() {
        final InMemoryTaskManager taskManager = new InMemoryTaskManager();

        final Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask1 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0), Duration.ofMinutes(30), epicId);
        final Subtask subtask2 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(30), Duration.ofMinutes(30), epicId);
        final Subtask subtask3 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(60), Duration.ofMinutes(30), epicId);
        final Subtask subtask4 = new Subtask("Задача", "Описание", Status.NEW, LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(90), Duration.ofMinutes(30), epicId);
        final Subtask subtask5 = new Subtask("Задача", "Описание", Status.NEW, null, Duration.ofMinutes(30), epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask3);
        taskManager.addNewSubtask(subtask5);
        taskManager.addNewSubtask(subtask2);
        int subtaskId = taskManager.addNewSubtask(subtask4);

        taskManager.deleteSubtask(subtaskId);

        final LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 15, 0);
        final LocalDateTime endTime = LocalDateTime.of(2024, 2, 1, 15, 0).plusMinutes(90);

        assertEquals(startTime, epic.getStartTime().get());
        assertEquals(endTime, epic.getEndTime().get());
    }
}