package tasks;

import org.junit.jupiter.api.Test;
import ru.tasks.Status;
import ru.tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    public void mustBeEqualIfIdIsEqual() {
        Subtask task1 = new Subtask("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now(), Duration.ZERO, 1);
        task1.setId(1);
        Subtask task2 = new Subtask("Задача 2", "Описание 2", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ZERO, 2);
        task2.setId(1);

        assertEquals(task1, task2);
    }
}