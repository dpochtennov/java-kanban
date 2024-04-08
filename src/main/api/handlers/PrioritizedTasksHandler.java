package main.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.api.HttpMethods;
import main.manager.taskManager.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends HttpTaskHandler {
    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().toString();
            HttpMethods requestMethod = HttpMethods.valueOf(exchange.getRequestMethod());
            logRequest(requestMethod, path);
            if (requestMethod.equals(HttpMethods.GET)) {
                handleGetRequest(exchange);
            } else {
                handleUnsupportedRequest(exchange);
            }

        } catch (IOException exception) {
            System.out.println("Graceful shutdown");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        writeResponse(exchange, response, 200);
    }
}
