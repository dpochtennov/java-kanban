package main.api;

import com.google.gson.reflect.TypeToken;
import main.api.handlers.SubTasksHandler;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubTasksHandlerTest extends HttpTaskHandlerTest<SubTasksHandler> {

    String apiUrl = "http://localhost:8080/subtasks";

    @Test
    void testCreateMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        SubTask subTask = new SubTask("Subtask", "description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN, Duration.ofMinutes(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Created", response.body());

        List<SubTask> tasksFromManager = taskManager.getAllSubTasks();
        SubTask savedTask = tasksFromManager.get(0);
        assertEquals(subTask.getName(), savedTask.getName());
        assertEquals(subTask.getDescription(), savedTask.getDescription());
    }

    @Test
    void testUpdateMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        SubTask subTask = taskManager.addSubTask(new SubTask("Subtask", "description", TaskStatus.NEW, epic.getId(),
                LocalDateTime.MIN, Duration.ofMinutes(1)));
        SubTask updatedSubTask = new SubTask(subTask.getId(), "new name", "new description",
                TaskStatus.IN_PROGRESS, subTask.getEpicId(), LocalDateTime.MIN, Duration.ofMinutes(1));
        String taskJson = gson.toJson(updatedSubTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", response.body());

        Task updatedTaskFromManager = taskManager.getSubTaskById(updatedSubTask.getId());
        EpicTask epicOfSubTask = taskManager.getEpicTaskById(updatedSubTask.getEpicId());
        assertEquals(updatedSubTask.getName(), updatedTaskFromManager.getName());
        assertEquals(updatedSubTask.getDescription(), updatedTaskFromManager.getDescription());
        assertEquals(updatedSubTask.getTaskStatus(), updatedTaskFromManager.getTaskStatus());
        assertEquals(TaskStatus.IN_PROGRESS, epicOfSubTask.getTaskStatus());
    }

    @Test
    void testIntersectionValidation() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        SubTask subTask = taskManager.addSubTask(new SubTask("Subtask", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        SubTask subTask2 = new SubTask( "new name", "new description",
                TaskStatus.IN_PROGRESS, subTask.getEpicId(), LocalDateTime.MIN, Duration.ofMinutes(1));

        String taskJson = gson.toJson(subTask2);
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
    void testNoEpicValidation() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Subtask", "description", TaskStatus.NEW, UUID.randomUUID(),
                LocalDateTime.MIN, Duration.ofMinutes(1));

        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals("No subtask epic exists", response.body());
    }

    @Test
    void testGetByIdMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        SubTask subTask = taskManager.addSubTask(new SubTask("Subtask", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task taskFromResponse = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());

        assertEquals(subTask.getId(), taskFromResponse.getId());
        assertEquals(subTask.getName(), taskFromResponse.getName());
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
    void testGetSubTaskListMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        taskManager.addSubTask(new SubTask("First", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        taskManager.addSubTask(new SubTask("Second", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN.plusMinutes(10), Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> tasksFromHttp = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasksFromHttp.size());
    }

    @Test
    void testDeleteByIdMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        SubTask subTask = taskManager.addSubTask(new SubTask("Subtask", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl + "/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("SubTask deleted", response.body());
        List<SubTask> tasksFromManager = taskManager.getAllSubTasks();
        assertEquals(0, tasksFromManager.size());
    }

    @Test
    void testDeleteMethod() throws IOException, InterruptedException {
        EpicTask epic = taskManager.addEpicTask(new EpicTask("Epic", "description"));
        taskManager.addSubTask(new SubTask("First", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN, Duration.ofMinutes(1)));
        taskManager.addSubTask(new SubTask("Second", "description", TaskStatus.NEW,
                epic.getId(), LocalDateTime.MIN.plusMinutes(10), Duration.ofMinutes(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(apiUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("SubTask list cleared", response.body());
        List<SubTask> tasksFromManager = taskManager.getAllSubTasks();
        assertEquals(0, tasksFromManager.size());
    }
}
