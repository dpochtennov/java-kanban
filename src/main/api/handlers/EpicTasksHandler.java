package main.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.api.HttpMethods;
import main.manager.taskManager.TaskManager;
import main.tasks.EpicTask;

import java.io.IOException;
import java.util.UUID;

public class EpicTasksHandler extends HttpTaskHandler {
    public EpicTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().toString();
            HttpMethods requestMethod = HttpMethods.valueOf(exchange.getRequestMethod());
            logRequest(requestMethod, path);
            switch (requestMethod) {
                case GET:
                    handleGetRequest(path, exchange);
                    break;
                case POST:
                    handlePostRequest(path, exchange);
                    break;
                case DELETE:
                    handleDeleteRequest(path, exchange);
                    break;
                default:
                    handleUnsupportedRequest(exchange);
            }

        } catch (IOException exception) {
            System.out.println("Graceful shutdown");
        }
    }

    private void handleGetRequest(String path, HttpExchange exchange) throws IOException {
        String[] pathElements = path.split("/");
        if (pathElements.length == 2) {
            String response = gson.toJson(taskManager.getAllEpics());
            writeResponse(exchange, response, 200);
            return;
        }

        try {
            UUID taskId = UUID.fromString(pathElements[2]);
            EpicTask epic = taskManager.getEpicTaskById(taskId);
            if (pathElements.length == 3) {
                String response = gson.toJson(epic);
                writeResponse(exchange, response, 200);
                return;
            }
            if (pathElements.length == 4 && pathElements[3].equals("subtasks")) {
                String response = gson.toJson(taskManager.getSubtasksOfEpic(epic.getId()));
                writeResponse(exchange, response, 200);
            } else {
                throw new RuntimeException("Path Not found");
            }
        } catch (RuntimeException exception) {
            writeResponse(exchange, "Not found", 404);
        }
    }

    private void handlePostRequest(String path, HttpExchange exchange) throws IOException {
        try {
            String[] pathElements = path.split("/");
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            EpicTask task = gson.fromJson(body, EpicTask.class);
            if (pathElements.length == 2) {
                taskManager.addEpicTask(task);
                writeResponse(exchange, "Created", 201);
                return;
            }
            taskManager.updateEpicTask(task);
            writeResponse(exchange, "Updated", 201);
        } catch (Exception exception) {
            writeResponse(exchange, "Server-side error: " + exception.getMessage(), 500);
        }
    }

    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException {
        String[] pathElements = path.split("/");

        if (pathElements.length == 2) {
            taskManager.clearEpicTaskLists();
            writeResponse(exchange, "Epics list cleared", 200);
            return;
        }
        UUID taskId = UUID.fromString(pathElements[2]);
        taskManager.removeEpicTaskById(taskId);
        writeResponse(exchange, "EpicTask deleted", 200);
    }
}
