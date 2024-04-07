package main.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import main.api.adapters.DurationAdapter;
import main.api.adapters.LocalDateTimeAdapter;
import main.api.adapters.TaskStatusAdapter;
import main.api.adapters.TaskTypesAdapter;
import main.api.handlers.*;
import main.manager.Managers;
import main.manager.taskManager.TaskManager;
import main.tasks.TaskStatus;
import main.tasks.TaskTypes;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int HTTP_PORT = 8080;
    private HttpServer server;
    private TaskManager manager;

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start(HTTP_PORT);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter());
        gsonBuilder.registerTypeAdapter(TaskTypes.class, new TaskTypesAdapter());
        return gsonBuilder.create();
    }

    public void start(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            Gson gson = getGson();

            manager = Managers.getDefault();
            server.createContext("/tasks", new TasksHandler(manager, gson));
            server.createContext("/subtasks", new SubTasksHandler(manager, gson));
            server.createContext("/epics", new EpicTasksHandler(manager, gson));
            server.createContext("/history", new TaskHistoryHandler(manager, gson));
            server.createContext("/prioritized", new PrioritizedTasksHandler(manager, gson));
            System.out.println("Starting server on port: " + HTTP_PORT);
            server.start();
        } catch (Exception e) {
            System.out.println("Failure during server start: " + e.getMessage());
        }
    }

    public void stop() {
        server.stop(0);
    }

    public TaskManager getManager() {
        return manager;
    }
}
