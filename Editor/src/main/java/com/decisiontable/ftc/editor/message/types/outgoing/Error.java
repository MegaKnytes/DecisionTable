package com.decisiontable.ftc.editor.message.types.outgoing;

import com.google.gson.JsonObject;
import com.decisiontable.ftc.editor.message.Message;

public class Error {
    public static JsonObject generateError(String friendlyErrorReason, String errorTrace){
        JsonObject json = new JsonObject();
        JsonObject error = new JsonObject();

        error.addProperty("friendlyErrorCause", friendlyErrorReason);
        error.addProperty("errorDebugTrace", errorTrace);

        json.addProperty("type", "ERROR");
        json.add("message", error);
        return json;
    }
}
