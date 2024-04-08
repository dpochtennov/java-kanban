package main.api;

import com.google.gson.Gson;
import main.api.handlers.HttpTaskHandler;
import main.manager.taskManager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static main.api.HttpTaskServer.HTTP_PORT;

abstract public class HttpTaskHandlerTest<T extends HttpTaskHandler> {
    protected TaskManager taskManager;
    protected HttpTaskServer httpTaskServer;
    protected final Gson gson = HttpTaskServer.getGson();


    @BeforeEach
    public void setUp() {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start(HTTP_PORT);
        taskManager = httpTaskServer.getManager();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }
}
