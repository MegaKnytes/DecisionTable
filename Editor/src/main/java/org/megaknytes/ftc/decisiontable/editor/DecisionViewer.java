package org.megaknytes.ftc.decisiontable.editor;


import static org.megaknytes.ftc.decisiontable.editor.HTTPHandler.bindWebServer;
import static org.megaknytes.ftc.decisiontable.editor.HTTPHandler.handleUpload;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.megaknytes.ftc.decisiontable.core.DTProcessor;
import org.megaknytes.ftc.decisiontable.editor.message.Message;
import org.megaknytes.ftc.decisiontable.editor.message.MessageDeserializer;
import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.util.WebHandlerManager;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.ftccommon.external.OnDestroy;
import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoWSD;

public class DecisionViewer {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .serializeNulls()
            .create();
    private static final Logger LOGGER = Logger.getLogger(DecisionViewer.class.getName());
    private static final DecisionViewer INSTANCE = new DecisionViewer();
    private NanoWSD server;

    public DecisionViewer() {}

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop){
        INSTANCE.server = new NanoWSD(11093) {
            @Override
            protected WebSocket openWebSocket(IHTTPSession handshake) {
                return new WebSocketHandler(handshake, eventLoop);
            }

            @Override
            public Response serveHttp(IHTTPSession session) {
                File decisionTablesDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
                if (!decisionTablesDir.exists()) {
                    decisionTablesDir.mkdirs();
                }

                if (session.getMethod() == Method.POST && session.getUri().equals("/file/upload")) {
                    return handleUpload(session, context, decisionTablesDir);
                } else if (session.getMethod() == Method.DELETE && session.getUri().equals("/file")) {
                    try {
                        List<String> files = session.getParameters().get("file");
                        assert files != null;
                        for (String filename : files) {
                            File fileToDelete = new File(decisionTablesDir, filename);
                            if (fileToDelete.exists()) {
                                fileToDelete.delete();
                            }
                        }
                        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "File deleted");
                    } catch (Exception e) {
                        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error deleting file: " + e.getMessage());
                    }
                } else if (session.getMethod() == Method.GET && session.getUri().equals("/file")) {
                    try {
                        List<String> files = session.getParameters().get("file");
                        assert files != null;
                        File fileToRead = new File(decisionTablesDir, files.get(0));
                        if (!fileToRead.exists()) {
                            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not found");
                        }

                        FileInputStream fis = new FileInputStream(fileToRead);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        StringBuilder fileContent = new StringBuilder();
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            fileContent.append(new String(buffer, 0, bytesRead));
                        }
                        fis.close();
                        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, fileContent.toString());
                    } catch (Exception e) {
                        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error reading file: " + e.getMessage());
                    }
                } else if (session.getMethod() == Method.GET && session.getUri().equals("/file/list")) {
                    try {
                        File[] files = decisionTablesDir.listFiles();
                        if (files == null) {
                            return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "[]");
                        }

                        StringBuilder fileList = new StringBuilder("[");
                        for (int i = 0; i < files.length; i++) {
                            fileList.append("\"").append(files[i].getName()).append("\"");
                            if (i < files.length - 1) {
                                fileList.append(", ");
                            }
                        }
                        fileList.append("]");
                        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, fileList.toString());
                    } catch (Exception e) {
                        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error listing files: " + e.getMessage());
                    }
                } else if (session.getMethod() == Method.GET && session.getUri().equals("/drivers/list")) {
                    return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "drivers");
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Method not allowed");
                }
            }
        };
        try {
            INSTANCE.server.start(-1);
            LOGGER.log(Level.INFO, "Websocket handler started on port " + INSTANCE.server.getListeningPort());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting websocket handler", e);
        }
    }

    @WebHandlerRegistrar
    public static void attachWebUI(Context context, WebHandlerManager manager) {
        bindWebServer(manager);
    }

    @OnDestroy
    public static void stop() {
        INSTANCE.server.stop();
        LOGGER.log(Level.INFO, "Websocket handler stopped");
    }
}