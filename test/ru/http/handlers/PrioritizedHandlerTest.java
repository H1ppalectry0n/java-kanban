package ru.http.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.InMemoryTaskManager;
import ru.TaskManager;
import ru.http.HttpTaskServer;
import ru.tasks.Status;
import ru.tasks.Task;

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

class PrioritizedHandlerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandlerTest() throws IOException {
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
    public void getPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Desc 1", Status.NEW, LocalDateTime.now().plusMinutes(6), Duration.ofMinutes(5));
        Task task2 = new Task("Task 1", "Desc 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест,
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // читаем содержимое ответа и пытаемся привести из [Json]  в [Tasks]
        String s = new String(response.body().getBytes(), StandardCharsets.UTF_8);
        class TaskToken extends TypeToken<List<Task>> {
        }

        assertDoesNotThrow(() -> {
            List<Task> tasks = gson.fromJson(s, new TaskToken().getType());
            assertEquals(2, tasks.size(), "Некорректное количество задач");
            assertEquals(task2, tasks.get(0));
            assertEquals(task1, tasks.get(1));
        });
    }

}