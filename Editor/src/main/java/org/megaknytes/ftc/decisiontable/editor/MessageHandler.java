package org.megaknytes.ftc.decisiontable.editor;


import org.megaknytes.ftc.decisiontable.core.DTProcessor;
import org.megaknytes.ftc.decisiontable.editor.message.Message;
import org.megaknytes.ftc.decisiontable.editor.message.types.incoming.InitOpMode;
import org.megaknytes.ftc.decisiontable.editor.message.types.outgoing.Heartbeat;
import org.megaknytes.ftc.decisiontable.editor.message.types.outgoing.Success;
import com.qualcomm.ftccommon.FtcEventLoop;
import org.megaknytes.ftc.decisiontable.editor.message.types.outgoing.Error;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());

    public static String handleMessage(FtcEventLoop eventLoop, Message message) {
        String response;
        try{
            switch (message.getType()) {
                case STOP_OPMODE:
                    eventLoop.getOpModeManager().requestOpModeStop(eventLoop.getOpModeManager().getActiveOpMode());
                    response = Success.generateSuccess().toString();
                    break;
                case START_OPMODE:
                    eventLoop.getOpModeManager().startActiveOpMode();
                    response = Success.generateSuccess().toString();
                    break;
                case INIT_OPMODE:
                    try {
                        eventLoop.getOpModeManager().initOpMode(((InitOpMode) message).getOpModeName());
                        response = Success.generateSuccess().toString();
                    } catch (NullPointerException e) {
                        LOGGER.log(Level.WARNING, "OpMode Name Not Provided", e);
                        response = Error.generateError("OpMode Name Not Provided", e.toString()).toString();
                    }
                    break;
                case RESTART_ROBOT:
                    try {
                        DTProcessor.getRobot().shutdown();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, e.toString());
                    }
                case HEARTBEAT:
                    try{
                        response = Heartbeat.getHeartbeat(
                                eventLoop.getOpModeManager().getActiveOpModeName(),
                                eventLoop.getOpModeManager().getRobotState(),
                                eventLoop.getOpModeManager().getActiveOpMode().hardwareMap.voltageSensor.iterator().next().getVoltage()
                        ).toString();
                    } catch (NullPointerException e) {
                        LOGGER.log(Level.WARNING, "Attempt to Generate Heartbeat while Robot Not Initialized", e);
                        response = Error.generateError("Attempt to Generate Heartbeat while Robot Not Initialized", e.toString()).toString();
                    }
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unknown message type: " + message.getType());
                    LOGGER.log(Level.WARNING, "Message: " + message);
                    response = Error.generateError("Unknown message type", "Unknown message type: " + message.getType()).toString();
                    break;
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Error handling message", e);
            response = Error.generateError("Malformed Message Received", e.toString()).toString();
        }
        return response;
    }
}