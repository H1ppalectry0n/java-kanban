package ru.http.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.InMemoryTaskManager;
import ru.TaskManager;
import ru.http.HttpTaskServer;
import ru.tasks.Epic;
import ru.tasks.Status;
import ru.tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public SubtasksHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));

        // создаём подзадачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epicId);
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddOverlappingSubtasks() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));

        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epicId);
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));

        // создаём задачу через manager, чтобы знать ее id
        Subtask subtask = new Subtask("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epicId);
        int taskId = manager.addNewSubtask(subtask);

        // удаляем задачу
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        List<Subtask> tasksFromManager = manager.getSubtasks();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteNonExistTask() throws IOException, InterruptedException {
        // удаляем задачу
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/12354");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        List<Subtask> tasksFromManager = manager.getSubtasks();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));

        // создаём задачу через manager, чтобы знать ее id
        Subtask subtask = new Subtask("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epicId);
        int taskId = manager.addNewSubtask(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест,
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // читаем содержимое ответа и пытаемся привести из Json  в Task
        String s = new String(response.body().getBytes(), StandardCharsets.UTF_8);
        assertDoesNotThrow(() -> {
            Subtask subtask2 = gson.fromJson(s, Subtask.class);
            assertEquals(subtask2, subtask);
        });
    }

    @Test
    public void testGetNonExistTask() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/12345");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест,
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));

        // Создаем задачи
        Subtask subtask1 = new Subtask("Test 1", "Testing task 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epicId);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now().plusMinutes(6), Duration.ofMinutes(5), epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // читаем содержимое ответа и пытаемся привести из [Json]  в [Tasks]
        String s = new String(response.body().getBytes(), StandardCharsets.UTF_8);
        class SubtaskToken extends TypeToken<List<Subtask>> {
        }

        assertDoesNotThrow(() -> {
            List<Subtask> tasks = gson.fromJson(s, new SubtaskToken().getType());
            assertEquals(2, tasks.size(), "Некорректное количество задач");
            assertEquals(subtask1, tasks.get(0));
            assertEquals(subtask2, tasks.get(1));
        });
    }

}