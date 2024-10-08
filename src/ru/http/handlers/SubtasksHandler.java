package ru.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.ManagerOverlapException;
import ru.ManagerSaveException;
import ru.TaskManager;
import ru.http.ResponseErrorMessage;
import ru.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
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
            }
        } catch (NumberFormatException e) {
            final String message = gson.toJson(new ResponseErrorMessage(404, "Неверно задан id."));
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
        switch (split.length) {
            case 2 -> {
                List<Subtask> subtasks = manager.getSubtasks();
                sendText(exchange, gson.toJson(subtasks));
            }
            case 3 -> {
                final Subtask subtask = manager.getSubtask(Integer.parseInt(split[2]));
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask));
                } else {
                    final String message = gson.toJson(new ResponseErrorMessage(404, "Подзадача не найдена."));
                    sendNotFound(exchange, message);
                }
            }
            default -> {
                final String message = gson.toJson(new ResponseErrorMessage(404, "Заданный эндпоинт не найден."));
                sendNotFound(exchange, message);
            }
        }
    }

    private void postRequest(HttpExchange exchange) throws IOException {
        String s = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(s, Subtask.class);
        try {
            if (subtask.getId() != null) {
                manager.updateSubtask(subtask);
            } else {
                manager.addNewSubtask(subtask);
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
            manager.deleteSubtask(taskId);
        }
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }
}
