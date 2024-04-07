package main.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.customExceptions.TaskIntersectedException;
import main.manager.taskManager.TaskManager;
import main.tasks.Task;

import java.io.IOException;
import java.util.UUID;

public class TasksHandler extends HttpTaskHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().toString();
            String requestMethod = exchange.getRequestMethod();
            logRequest(requestMethod, path);
            switch (requestMethod) {
                case "GET":
                    handleGetRequest(path, exchange);
                    break;
                case "POST":
                    handlePostRequest(path, exchange);
                    break;
                case "DELETE":
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
            String response = gson.toJson(taskManager.getAllTasks());
            writeResponse(exchange, response, 200);
            return;
        }

        try {
            UUID taskId = UUID.fromString(pathElements[2]);
            Task task = taskManager.getTaskById(taskId);
            System.out.println(task);
            String response = gson.toJson(task);
            writeResponse(exchange, response, 200);
        } catch (RuntimeException exception) {
            writeResponse(exchange, "Not found", 404);
        }
    }

    private void handlePostRequest(String path, HttpExchange exchange) throws IOException {
        try {
            String[] pathElements = path.split("/");
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            if (pathElements.length == 2) {
                taskManager.addTask(task);
                writeResponse(exchange, "Created", 201);
                return;
            }
            taskManager.updateTask(task);
            writeResponse(exchange, "Updated", 201);
        } catch (TaskIntersectedException exception) {
            writeResponse(exchange, "Can not be added due to validation constrains", 406);
        } catch (Exception exception) {
            writeResponse(exchange, "Server-side error: " + exception.getMessage(), 500);
        }
    }

    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException {
        String[] pathElements = path.split("/");
        if (pathElements.length == 2) {
            taskManager.clearTaskList();
            writeResponse(exchange, "Task list cleared", 200);
            return;
        }
        UUID taskId = UUID.fromString(pathElements[2]);
        taskManager.removeTaskById(taskId);
        writeResponse(exchange, "Task deleted", 200);
    }
}