package com.decisiontable.ftc.editor.message.types;

import com.decisiontable.ftc.editor.message.types.incoming.InitOpMode;
import com.decisiontable.ftc.editor.message.types.incoming.StartOpMode;
import com.decisiontable.ftc.editor.message.types.incoming.StopOpMode;
import com.decisiontable.ftc.editor.message.Message;
import com.decisiontable.ftc.editor.message.types.outgoing.Heartbeat;

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