package main.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.customExceptions.NoSubTaskEpicException;
import main.customExceptions.TaskIntersectedException;
import main.manager.taskManager.TaskManager;
import main.tasks.SubTask;

import java.io.IOException;
import java.util.UUID;

public class SubTasksHandler extends HttpTaskHandler {
    public SubTasksHandler(TaskManager taskManager, Gson gson) {
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
            String response = gson.toJson(taskManager.getAllSubTasks());
            writeResponse(exchange, response, 200);
            return;
        }

        try {
            UUID taskId = UUID.fromString(pathElements[2]);
            SubTask subTask = taskManager.getSubTaskById(taskId);
            System.out.println(subTask);
            String response = gson.toJson(subTask);
            writeResponse(exchange, response, 200);
        } catch (RuntimeException exception) {
            writeResponse(exchange, "Not found", 404);
        }
    }

    private void handlePostRequest(String path, HttpExchange exchange) throws IOException {
        try {
            String[] pathElements = path.split("/");
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (pathElements.length == 2) {
                taskManager.addSubTask(subTask);
                writeResponse(exchange, "Created", 201);
                return;
            }
            taskManager.updateSubTask(subTask);
            writeResponse(exchange, "Updated", 201);
        } catch (TaskIntersectedException exception) {
            writeResponse(exchange, "Can not be added due to validation constrains", 406);
        } catch (NoSubTaskEpicException exception) {
            writeResponse(exchange, "No subtask epic exists", 406);
        } catch (Exception exception) {
            writeResponse(exchange, "Server-side error: " + exception.getMessage(), 500);
        }
    }

    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException {
        String[] pathElements = path.split("/");

        if (pathElements.length == 2) {
            taskManager.clearSubTaskLists();
            writeResponse(exchange, "SubTask list cleared", 200);
            return;
        }
        UUID taskId = UUID.fromString(pathElements[2]);
        taskManager.removeSubTaskById(taskId);
        writeResponse(exchange, "SubTask deleted", 200);
    }
}
