package main.api;

import com.google.gson.reflect.TypeToken;
import main.api.handlers.TaskHistoryHandler;
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

public class TaskHistoryHandlerTest extends HttpTaskHandlerTest<TaskHistoryHandler> {
    String apiUrl = "http://localhost:8080/history";

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(new Task("First", "description", TaskStatus.NEW,
                LocalDateTime.MIN, Duration.ofMinutes(5)));
        Task task2 = taskManager.addTask(new Task("Second", "description", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(10), Duration.ofMinutes(5)));
        taskManager.addTask(new Task("Second", "description", TaskStatus.NEW,
                LocalDateTime.MIN.plusMinutes(20), Duration.ofMinutes(5)));
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromHttp = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getHistory().toString(), tasksFromHttp.toString());
    }
}
