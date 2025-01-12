package com.megaknytes.ftc.decisionviewer.message.types.outgoing;

import com.google.gson.JsonObject;
import com.megaknytes.ftc.decisionviewer.message.Message;

public class Success {
    public static JsonObject generateSuccess(){
        JsonObject json = new JsonObject();
        json.addProperty("type", "RESPONSE");
        json.addProperty("message", "SUCCESS");
        return json;
    }
}
