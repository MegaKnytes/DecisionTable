package org.megaknytes.ftc.decisiontable.editor.message;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.megaknytes.ftc.decisiontable.editor.message.types.MessageType;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {
    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject message = jsonElement.getAsJsonObject();
        MessageType messageType = jsonDeserializationContext.deserialize(message.get("type"), MessageType.class);
        if (messageType == null || messageType.messageClass == null) {
            return null;
        }
        Type msgType = TypeToken.get(messageType.messageClass).getType();
        return jsonDeserializationContext.deserialize(jsonElement, msgType);
    }
}