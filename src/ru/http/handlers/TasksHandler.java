package ru.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.ManagerOverlapException;
import ru.ManagerSaveException;
import ru.TaskManager;
import ru.http.ResponseErrorMessage;
import ru.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    getRequest(exchange);
                }
                case "POST" -> {
                    postRequest(exchange);
                }
                case "DELETE" -> {
                    deleteRequest(exchange);
                }
                default -> {
                    final String message = gson.toJson(new ResponseErrorMessage(404, "Неизвестная команда."));
                    sendNotFound(exchange, message);
                }
            }
        } catch (NumberFormatException e) {
            final String message = gson.toJson(new ResponseErrorMessage(404, "Неверно задан id задачи."));
            sendNotFound(exchange, message);
        } catch (JsonSyntaxException e) {
            final String message = gson.toJson(new ResponseErrorMessage(404, "Неверный синтаксис."));
            sendNotFound(exchange, message);
        } catch (ManagerSaveException e) {
            final String message = gson.toJson(new ResponseErrorMessage(500, "Ошибка сохранения."));
            sendHasInteractions(exchange, message);
        }
    }

    private void getRequest(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        if (split.length == 2) {
            List<Task> tasks = manager.getTasks();
            sendText(exchange, gson.toJson(tasks));
        } else if (split.length == 3) {
            final Task task = manager.getTask(Integer.parseInt(split[2]));
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                final String message = gson.toJson(new ResponseErrorMessage(404, "Задача не найдена."));
                sendNotFound(exchange, message);
            }
        } else {
            final String message = gson.toJson(new ResponseErrorMessage(404, "Заданный эндпоинт не найден."));
            sendNotFound(exchange, message);
        }
    }

    private void postRequest(HttpExchange exchange) throws IOException {
        String s = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(s, Task.class);
        try {
            if (task.getId() != null) {
                manager.updateTask(task);
            } else {
                manager.addNewTask(task);
            }
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (ManagerOverlapException e) {
            String message = gson.toJson(new ResponseErrorMessage(406, e.getMessage()));
            sendOverlapTask(exchange, message);
        }
    }

    private void deleteRequest(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        if (split.length == 3) {
            final int taskId = Integer.parseInt(split[2]);
            manager.deleteTask(taskId);
        }
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }
}
