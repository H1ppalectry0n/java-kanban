package ru.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.Managers;
import ru.TaskManager;
import ru.http.adapters.DurationAdapter;
import ru.http.adapters.LocalDateTimeAdapter;
import ru.http.handlers.*;
import ru.tasks.Epic;
import ru.tasks.Status;
import ru.tasks.Subtask;
import ru.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    private final HttpServer server;

    public HttpTaskServer(final TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TasksHandler(manager, GSON));
        server.createContext("/subtasks", new SubtasksHandler(manager, GSON));
        server.createContext("/epics", new EpicsHandler(manager, GSON));
        server.createContext("/history", new HistoryHandler(manager, GSON));
        server.createContext("/prioritized", new PrioritizedHandler(manager, GSON));
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        manager.addNewTask(new Task("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now(), Duration.ofDays(1)));
        manager.addNewTask(new Task("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(1), Duration.ofDays(1)));
        manager.addNewTask(new Task("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(2), Duration.ofDays(1)));
        manager.addNewTask(new Task("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(3), Duration.ofDays(1)));
        manager.addNewTask(new Task("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(4), Duration.ofDays(1)));
        final int epicId = manager.addNewEpic(new Epic("asfafasf", "asdadadasdad"));
        manager.addNewSubtask(new Subtask("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(5), Duration.ofDays(1), epicId));
        manager.addNewSubtask(new Subtask("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(6), Duration.ofDays(1), epicId));
        manager.addNewSubtask(new Subtask("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(7), Duration.ofDays(1), epicId));
        manager.addNewSubtask(new Subtask("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(8), Duration.ofDays(1), epicId));
        manager.addNewSubtask(new Subtask("asfasfas", "sfafasfasf", Status.NEW, LocalDateTime.now().plusDays(9), Duration.ofDays(1), epicId));


        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    public static Gson getGson() {
        return GSON;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
