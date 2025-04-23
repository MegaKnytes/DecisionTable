package com.decisiontable.ftc.editor.message.types.outgoing;

import com.google.gson.JsonObject;
import com.decisiontable.ftc.editor.message.Message;
import com.qualcomm.robotcore.robot.RobotState;

public class Heartbeat extends Message {
    public static JsonObject getHeartbeat(String runningOpMode, RobotState opModeState, double batteryVoltage){
        JsonObject json = new JsonObject();
        JsonObject heartbeat = new JsonObject();
        json.addProperty("type", "RESPONSE");
        heartbeat.addProperty("runningOpMode", runningOpMode);
        heartbeat.addProperty("opModeState", opModeState.toString());
        heartbeat.addProperty("batteryVoltage", batteryVoltage);
        json.add("message", heartbeat);
        return json;
    }
}
