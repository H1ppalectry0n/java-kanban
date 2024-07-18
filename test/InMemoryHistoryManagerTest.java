import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void addedTasksNotChange() {
        final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        final Task task = new Task("Задача 1", "Описание 2", Status.NEW);
        historyManager.add(task);

        task.setId(2);

        final ArrayList<Task> history = historyManager.getHistory();

        assertNotEquals(task, history.getFirst());
    }
}