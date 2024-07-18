package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    public void mustBeEqualIfIdIsEqual() {
        Subtask task1 = new Subtask("Задача 1", "Описание 1", Status.NEW, 1);
        task1.setId(1);
        Subtask task2 = new Subtask("Задача 2", "Описание 2", Status.IN_PROGRESS, 2);
        task2.setId(1);

        assertEquals(task1, task2);
    }
}