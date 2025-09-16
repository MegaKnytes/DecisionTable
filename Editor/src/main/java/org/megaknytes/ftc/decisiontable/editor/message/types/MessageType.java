package org.megaknytes.ftc.decisiontable.editor.message.types;

import org.megaknytes.ftc.decisiontable.editor.message.types.incoming.InitOpMode;
import org.megaknytes.ftc.decisiontable.editor.message.types.incoming.RestartRobot;
import org.megaknytes.ftc.decisiontable.editor.message.types.incoming.StartOpMode;
import org.megaknytes.ftc.decisiontable.editor.message.types.incoming.StopOpMode;
import org.megaknytes.ftc.decisiontable.editor.message.Message;
import org.megaknytes.ftc.decisiontable.editor.message.types.outgoing.Heartbeat;

public enum MessageType {
    HEARTBEAT(Heartbeat.class),
    INIT_OPMODE(InitOpMode.class),
    RESTART_ROBOT(RestartRobot.class),
    START_OPMODE(StartOpMode.class),
    STOP_OPMODE(StopOpMode.class);

    public final Class<? extends Message> messageClass;

    MessageType(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }
}