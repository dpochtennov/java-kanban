package main.api.adapters;

import com.google.gson.*;
import main.tasks.TaskStatus;

import java.lang.reflect.Type;

public class TaskStatusAdapter implements JsonSerializer<TaskStatus>, JsonDeserializer<TaskStatus> {

    @Override
    public TaskStatus deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return TaskStatus.valueOf(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(TaskStatus taskStatus, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(taskStatus.toString());
    }
}