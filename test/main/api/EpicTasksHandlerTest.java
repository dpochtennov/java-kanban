package main.api;

import com.google.gson.reflect.TypeToken;
import main.api.handlers.EpicTasksHandler;
import main.tasks.EpicTask;
import main.tasks.SubTask;
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

public class EpicTasksHandlerTest extends HttpTaskHandlerTest<EpicTasksHandler> {
    String apiUrl = "http://localhost:8080/epics";

    @Test
    void testCreateMethod() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("Test Epic", "description");
        String taskJson = gson.toJson(epicTask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Created", response.body());

        List<EpicTask> tasksFromManager = taskManager.getAllEpics();
        EpicTask savedTask = tasksFromManager.get(0);
        assertEquals(epicTask.getName(), savedTask.getName());
        assertEquals(epicTask.getDescription(), savedTask.getDescription());
    }

    @Test
    void testUpdateMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Test Epic", "description"));
        EpicTask updatedEpic = new EpicTask(epic.getId(), "Updated", "Updated");
        String taskJson = gson.toJson(updatedEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", response.body());

        EpicTask updatedTaskFromManager = taskManager.getEpicTaskById(updatedEpic.getId());
        assertEquals(updatedEpic.getName(), updatedTaskFromManager.getName());
        assertEquals(updatedEpic.getDescription(), updatedTaskFromManager.getDescription());
        assertEquals(updatedEpic.getTaskStatus(), updatedTaskFromManager.getTaskStatus());
    }

    @Test
    void testGetByIdMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Test Epic", "description"));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        EpicTask taskFromResponse = gson.fromJson(response.body(), new TypeToken<EpicTask>() {
        }.getType());

        assertEquals(epic.getId(), taskFromResponse.getId());
        assertEquals(epic.getName(), taskFromResponse.getName());
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
    void testGetEpicsListMethod() throws IOException, InterruptedException {
        taskManager.addEpicTask(new EpicTask("First", "description"));
        taskManager.addEpicTask(new EpicTask("Second", "description"));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<EpicTask> tasksFromHttp = gson.fromJson(response.body(), new TypeToken<List<EpicTask>>() {
        }.getType());
        assertEquals(2, tasksFromHttp.size());
    }

    @Test
    void testGetEpicsSubTaskListMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        taskManager.addSubTask(new SubTask("Subtask1", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        taskManager.addSubTask(new SubTask("Subtask2", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN.plusMinutes(10), Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + epic.getId().toString() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> tasksFromHttp = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        assertEquals(2, tasksFromHttp.size());
    }

    @Test
    void testGetEpicsSubTaskListWrongUrlMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        taskManager.addSubTask(new SubTask("Subtask1", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        taskManager.addSubTask(new SubTask("Subtask2", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN.plusMinutes(10), Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + epic.getId().toString() + "/random_path");
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
    void testGetEpicsSubTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/qwerty/subtasks");
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
    void testDeleteByIdMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("EpicTask deleted", response.body());
        List<EpicTask> tasksFromManager = taskManager.getAllEpics();
        assertEquals(0, tasksFromManager.size());
    }

    @Test
    void testDeleteMethod() throws IOException, InterruptedException {
        taskManager.addEpicTask(new EpicTask("First", "description"));
        taskManager.addEpicTask(new EpicTask("Second", "description"));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Epics list cleared", response.body());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(0, tasksFromManager.size());
    }
}
