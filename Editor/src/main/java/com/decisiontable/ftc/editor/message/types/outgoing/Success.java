package com.decisiontable.ftc.editor.message.types.outgoing;

import com.google.gson.JsonObject;
import com.decisiontable.ftc.editor.message.Message;

public class Success {
    public static JsonObject generateSuccess(){
        JsonObject json = new JsonObject();
        json.addProperty("type", "RESPONSE");
        json.addProperty("message", "SUCCESS");
        return json;
    }
}
