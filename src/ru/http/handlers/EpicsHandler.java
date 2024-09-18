package ru.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ManagerOverlapException;
import ru.ManagerSaveException;
import ru.TaskManager;
import ru.http.ResponseErrorMessage;
import ru.tasks.Epic;
import ru.tasks.EpicApi;
import ru.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(final TaskManager manager, final Gson gson) {
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

    public void getRequest(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        if (split.length == 2) {
            sendEpics(exchange);
        } else if (split.length == 3) {
            sendEpicById(exchange, Integer.parseInt(split[2]));
        } else if (split.length == 4 && split[3].equals("subtasks")) {
            sendEpicsSubtasks(exchange, Integer.parseInt(split[2]));
        } else {
            final String message = gson.toJson(new ResponseErrorMessage(404, "Заданный эндпоинт не найден."));
            sendNotFound(exchange, message);
        }
    }

    private void sendEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpics();
        sendText(exchange, gson.toJson(epics));
    }

    private void sendEpicById(HttpExchange exchange, int epicId) throws IOException {
        final Epic epic = manager.getEpic(epicId);
        if (epic != null) {
            sendText(exchange, gson.toJson(epic));
        } else {
            sendEpicNotFound(exchange);
        }
    }

    private void sendEpicsSubtasks(HttpExchange exchange, int epicId) throws IOException {
        final Epic epic = manager.getEpic(epicId);
        if (epic != null) {
            List<Subtask> subtasks = new ArrayList<>();
            for (int id : epic.getSubtaskIds()) {
                subtasks.add(manager.getSubtask(id));
            }
            sendText(exchange, gson.toJson(subtasks));
        } else {
            sendEpicNotFound(exchange);
        }
    }

    private void sendEpicNotFound(HttpExchange exchange) throws IOException {
        final String message = gson.toJson(new ResponseErrorMessage(404, "Эпик не найден."));
        sendNotFound(exchange, message);
    }

    public void postRequest(HttpExchange exchange) throws IOException {
        String s = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        // класс для создания и изменения эпиков
        EpicApi epicApi = gson.fromJson(s, EpicApi.class);
        try {
            if (epicApi.id != null) {
                Epic epic = manager.getEpic(epicApi.id);
                if (epic != null) {
                    epic.setName(epicApi.name);
                    epic.setDescription(epicApi.description);
                    manager.updateEpic(epic);
                }
            } else {
                manager.addNewEpic(new Epic(epicApi.name, epicApi.description));
            }
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (ManagerOverlapException e) {
            String message = gson.toJson(new ResponseErrorMessage(406, e.getMessage()));
            sendOverlapTask(exchange, message);
        }
    }

    public void deleteRequest(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        if (split.length == 3) {
            final int epicId = Integer.parseInt(split[2]);
            manager.deleteEpic(epicId);
        }
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }

}
