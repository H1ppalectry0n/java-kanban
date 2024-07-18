package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void mustBeEqualIfIdIsEqual() {
        Epic task1 = new Epic("Задача 1", "Описание 1");
        task1.setId(1);
        Epic task2 = new Epic("Задача 2", "Описание 2");
        task2.setId(1);

        assertEquals(task1, task2);
    }
}