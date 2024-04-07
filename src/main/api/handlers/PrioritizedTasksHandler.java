package main.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
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
            String requestMethod = exchange.getRequestMethod();
            logRequest(requestMethod, path);
            if (requestMethod.equals("GET")) {
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
