package com.megaknytes.ftc.decisionviewer.message.types;

import com.megaknytes.ftc.decisionviewer.message.types.incoming.InitOpMode;
import com.megaknytes.ftc.decisionviewer.message.types.incoming.StartOpMode;
import com.megaknytes.ftc.decisionviewer.message.types.incoming.StopOpMode;
import com.megaknytes.ftc.decisionviewer.message.Message;
import com.megaknytes.ftc.decisionviewer.message.types.outgoing.Heartbeat;

public enum MessageType {
    HEARTBEAT(Heartbeat.class),
    INIT_OPMODE(InitOpMode.class),
    START_OPMODE(StartOpMode.class),
    STOP_OPMODE(StopOpMode.class);

    public final Class<? extends Message> messageClass;

    MessageType(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }
}