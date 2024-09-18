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
import ru.tasks.EpicApi;
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

class EpicsHandlerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public EpicsHandlerTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        // Создание епика
        EpicApi epic = new EpicApi(null, "Epic 1", "Desc 1");
        // конвертируем в JSON
        String epicString = gson.toJson(epic);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicString)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        List<Epic> tasksFromManager = manager.getEpics();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteNonExistTask() throws IOException, InterruptedException {
        // удаляем задачу
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/12354");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        List<Epic> tasksFromManager = manager.getEpics();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        Epic epic1 = new Epic("Epic", "Descr");
        final int epicId = manager.addNewEpic(epic1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест,
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // читаем содержимое ответа и пытаемся привести из Json  в Task
        String s = new String(response.body().getBytes(), StandardCharsets.UTF_8);
        assertDoesNotThrow(() -> {
            Epic epic2 = gson.fromJson(s, Epic.class);
            assertEquals(epic1, epic2);
        });
    }

    @Test
    public void testGetNonExistTask() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/12345");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест,
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        // добаление эпика
        Epic epic1 = new Epic("Epic", "Descr");
        Epic epic2 = new Epic("Epic", "Descr");
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // читаем содержимое ответа и пытаемся привести из [Json]  в [Tasks]
        String s = new String(response.body().getBytes(), StandardCharsets.UTF_8);
        class EpicsToken extends TypeToken<List<Epic>> {
        }

        assertDoesNotThrow(() -> {
            List<Epic> tasks = gson.fromJson(s, new EpicsToken().getType());
            assertEquals(2, tasks.size(), "Некорректное количество задач");
            assertEquals(epic1, tasks.get(0));
            assertEquals(epic2, tasks.get(1));
        });
    }

    @Test
    public void testGetAllEpicsSubtasks() throws IOException, InterruptedException {
        // добаление эпика для подзадачи
        final int epicId = manager.addNewEpic(new Epic("Epic", "Descr"));

        // Создаем задачи
        Subtask subtask1 = new Subtask("Test 1", "Testing task 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epicId);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now().plusMinutes(6), Duration.ofMinutes(5), epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
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