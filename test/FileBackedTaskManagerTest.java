import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    public void saveEmptyFile() throws IOException {
        File file = Files.createTempFile("FileBackedTaskManagerTest", ".csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        manager.save();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            assertEquals(br.readLine(), "id,type,name,status,description,epic");

            // Проверка, что в файле больше ничего нет
            assertFalse(br.ready());
        }
    }

    @Test
    public void readFromEmptyFile() throws IOException {
        File file = Files.createTempFile("FileBackedTaskManagerTest", ".csv").toFile();
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void saveSomeTasks() throws IOException {
        File file = Files.createTempFile("FileBackedTaskManagerTest", ".csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.DONE);

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");

        int epicId = manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.DONE, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE, epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            assertEquals(br.readLine(), "id,type,name,status,description,epic");
            assertEquals(br.readLine(), "0,TASK,Задача 1,NEW,Описание 1,");
            assertEquals(br.readLine(), "1,TASK,Задача 2,DONE,Описание 2,");
            assertEquals(br.readLine(), "2,EPIC,Эпик 1,DONE,Описание 1,");
            assertEquals(br.readLine(), "3,EPIC,Эпик 2,NEW,Описание 2,");
            assertEquals(br.readLine(), "4,SUBTASK,Subtask 1,DONE,Description 1,2");
            assertEquals(br.readLine(), "5,SUBTASK,Subtask 2,DONE,Description 2,2");

            // Проверка, что в файле больше ничего нет
            assertFalse(br.ready());
        }
    }

    @Test
    public void loadTaskFromFile() throws IOException {
        File file = Files.createTempFile("FileBackedTaskManagerTest", ".csv").toFile();
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.write("0,TASK,Задача 1,NEW,Описание 1,\n");
            fileWriter.write("1,TASK,Задача 2,DONE,Описание 2,\n");
            fileWriter.write("2,EPIC,Эпик 1,DONE,Описание 1,\n");
            fileWriter.write("3,EPIC,Эпик 2,NEW,Описание 2,\n");
            fileWriter.write("4,SUBTASK,Subtask 1,DONE,Description 1,2\n");
            fileWriter.write("5,SUBTASK,Subtask 2,DONE,Description 2,2\n");
        }

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, manager.getEpics().size());
        assertEquals(2, manager.getSubtasks().size());
        assertEquals(2, manager.getTasks().size());

        assertEquals("Задача 1", manager.getTasks().getFirst().getName());
    }
}