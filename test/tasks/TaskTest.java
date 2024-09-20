package tasks;

import org.junit.jupiter.api.Test;
import ru.tasks.Status;
import ru.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    public void mustBeEqualIfIdIsEqual() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ZERO);
        task2.setId(1);

        assertEquals(task1, task2);
    }
}