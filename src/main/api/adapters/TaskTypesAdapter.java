package main.api.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import main.tasks.TaskTypes;

import java.lang.reflect.Type;

public class TaskTypesAdapter implements JsonSerializer<TaskTypes> {
    @Override
    public JsonElement serialize(TaskTypes taskType, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(taskType.toString());
    }
}
