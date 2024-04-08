package main.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.api.HttpMethods;
import main.manager.taskManager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class HttpTaskHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected HttpTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        System.out.printf("Responding with %s and body %s%n", responseCode, responseString);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    protected void logRequest(HttpMethods method, String path) {
        System.out.printf("Incoming HTTP request with %s method, path: %s%n", method.name(), path);
    }

    protected void handleUnsupportedRequest(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Method not supported", 405);
    }
}
