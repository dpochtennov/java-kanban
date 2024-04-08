package main.api;

import com.google.gson.reflect.TypeToken;
import main.api.handlers.TasksHandler;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TasksHandlerTest extends HttpTaskHandlerTest<TasksHandler> {

    String apiUrl = "http://localhost:8080/tasks";

    @Test
    void testCreateMethod() throws IOException, InterruptedException {
        Task task = new Task("Test task", "description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Created", response.body());

        List<Task> tasksFromManager = taskManager.getAllTasks();
        Task savedTask = tasksFromManager.get(0);
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
    }

    @Test
    void testUpdateMethod() throws IOException, InterruptedException {
        Task task = taskManager.addTask(
                new Task("Test task", "description", TaskStatus.NEW, LocalDateTime.now(),
                        Duration.ofMinutes(5)
                )
        );
        Task updatedTask = new Task(task.getId(), "Updated", "Updated", TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(updatedTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", response.body());

        Task updatedTaskFromManager = taskManager.getTaskById(updatedTask.getId());
        assertEquals(updatedTask.getName(), updatedTaskFromManager.getName());
        assertEquals(updatedTask.getDescription(), updatedTaskFromManager.getDescription());
        assertEquals(updatedTask.getTaskStatus(), updatedTaskFromManager.getTaskStatus());
    }

    @Test
    void testGetByIdMethod() throws IOException, InterruptedException {
        Task task = taskManager.addTask(new Task("Test task", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(url)
            .header("Accept", "application/json")
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task taskFromResponse = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());

        assertEquals(task.getId(), taskFromResponse.getId());
        assertEquals(task.getName(), taskFromResponse.getName());
    }

    @Test
    void testGetNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/qwerty");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Not found", response.body());
    }

    @Test
    void testGetTaskListMethod() throws IOException, InterruptedException {
        taskManager.addTask(new Task("First", "description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5)));
        taskManager.addTask(new Task("Second", "Description", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromHttp = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasksFromHttp.size());
    }

    @Test
    void testIntersectionValidation() throws IOException, InterruptedException {
        taskManager.addTask(new Task("First", "description", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        Task task2 = new Task("Second", "Description", TaskStatus.NEW, LocalDateTime.MIN,
                Duration.ofMinutes(1));

        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals("Can not be added due to validation constrains", response.body());
    }

    @Test
    void testDeleteByIdMethod() throws IOException, InterruptedException {
        Task task = taskManager.addTask(new Task("First", "description", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Task deleted", response.body());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(0, tasksFromManager.size());
    }

    @Test
    void testDeleteMethod() throws IOException, InterruptedException {
        taskManager.addTask(new Task("First", "description", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        taskManager.addTask(new Task("Second", "description", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(10), Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Task list cleared", response.body());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(0, tasksFromManager.size());
    }
}
