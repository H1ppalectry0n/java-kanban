package ru.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ManagerSaveException;
import ru.TaskManager;
import ru.http.ResponseErrorMessage;
import ru.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    final private TaskManager manager;
    final private Gson gson;

    public HistoryHandler(final TaskManager manager, final Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                List<Task> history = manager.getHistory();
                sendText(exchange, gson.toJson(history));
            } else {
                final String message = gson.toJson(new ResponseErrorMessage(404, "Неизвестная команда."));
                sendNotFound(exchange, message);
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
}
