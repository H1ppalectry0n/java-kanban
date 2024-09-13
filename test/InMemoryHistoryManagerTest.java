import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void addedTasksNotChange() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        final Task task = new Task("Задача 1", "Описание 2", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        historyManager.add(task);

        task.setId(4);

        final ArrayList<Task> history = historyManager.getHistory();

        assertNotEquals(task, history.getFirst());
    }

    @Test
    public void deleteTaskFromHistory() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        final Task task = new Task("Задача 1", "Описание 2", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        historyManager.add(task);

        historyManager.remove(task.getId());

        final ArrayList<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void addMoreThen10Task() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int i = 0; i < 20; i++) {
            final Task task = new Task("asdads", "dasdaad", Status.NEW, LocalDateTime.now(), Duration.ZERO);
            task.setId(i);
            historyManager.add(task);
        }

        final ArrayList<Task> history = historyManager.getHistory();

        assertEquals(history.size(), 20);
    }

    @Test
    public void orderOfTaskInHistory1() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        final ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final Task task = new Task("asdads", "dasdaad", Status.NEW, LocalDateTime.now(), Duration.ZERO);
            task.setId(i);
            tasks.add(task);
            historyManager.add(task);
        }

        final ArrayList<Task> history = historyManager.getHistory();

        assertEquals(history, tasks);
    }

    @Test
    public void orderOfTaskInHistory2() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        final ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final Task task = new Task("asdads", "dasdaad", Status.NEW, LocalDateTime.now(), Duration.ZERO);
            task.setId(i);
            tasks.add(task);
        }

        // перемешивание массива
        Task task = tasks.get(1);
        tasks.set(1, tasks.get(2));
        tasks.set(2, task);

        for (Task task1 : tasks) {
            historyManager.add(task1);
        }

        final ArrayList<Task> history = historyManager.getHistory();

        assertEquals(history, tasks);
    }

    @Test
    public void removeDuplicate() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        final Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        task1.setId(1);
        final Task task1d = new Task(task1);
        final Task task2 = new Task("Задача 2", "Описание 2", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        task2.setId(2);
        final Task task2d = new Task(task2);

        historyManager.add(task1);
        historyManager.add(task1d);
        historyManager.add(task2);
        historyManager.add(task2d);


        final ArrayList<Task> history = historyManager.getHistory();

        assertEquals(history.size(), 2);
        assertEquals(history.get(0), task1d);
        assertEquals(history.get(1), task2d);
    }

}