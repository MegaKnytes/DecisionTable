package org.megaknytes.ftc.decisiontable.editor;

import org.megaknytes.ftc.decisiontable.editor.message.Message;
import com.qualcomm.ftccommon.FtcEventLoop;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class WebSocketHandler extends NanoWSD.WebSocket {
    private static final Logger LOGGER = Logger.getLogger(WebSocketHandler.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final FtcEventLoop eventLoop;

    public WebSocketHandler(NanoHTTPD.IHTTPSession handshakeRequest, FtcEventLoop eventLoop) {
        super(handshakeRequest);
        this.eventLoop = eventLoop;
    }


    @Override
    protected void onOpen() {
        try {
            this.send("Connection established");
            LOGGER.log(Level.INFO, "Connection established");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message", e);
        }
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        LOGGER.log(Level.INFO, "Connection closed: " + reason);
        scheduler.shutdown();
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
        try {
            send(MessageHandler.handleMessage(eventLoop, DecisionViewer.GSON.fromJson(message.getTextPayload(), Message.class)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) {
        LOGGER.log(Level.FINE, "Received pong: " + Arrays.toString(pong.getBinaryPayload()));
    }

    @Override
    protected void onException(IOException exception) {
        LOGGER.log(Level.SEVERE, "WebSocket exception", exception);
        if (exception instanceof SocketTimeoutException) {
            LOGGER.log(Level.INFO, "SocketTimeoutException occurred. Closing connection.");
            try {
                this.close(NanoWSD.WebSocketFrame.CloseCode.AbnormalClosure, "Timeout occurred", false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing EditorWebSocket connection", e);
            }
        } else {
            LOGGER.log(Level.SEVERE, "Unknown exception occurred. Closing connection", exception);
            try {
                this.close(NanoWSD.WebSocketFrame.CloseCode.InternalServerError, "Unknown error occurred", false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing EditorWebSocket connection", e);
            }
        }
    }
}