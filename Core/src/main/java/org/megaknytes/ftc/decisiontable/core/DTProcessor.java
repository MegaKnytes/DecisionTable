package org.megaknytes.ftc.decisiontable.core;

import android.content.Context;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.ftc.decisiontable.core.utils.DTClassDiscoveryUtil;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.DTFileDiscovery;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.DecisionTable;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Rule;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.megaknytes.ftc.decisiontable.core.xml.XMLProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTProcessor {
    private final XMLProcessor xmlProcessor = new XMLProcessor();
    private static final DTProcessor INSTANCE = new DTProcessor();
    private final List<Rule> loadedRules = new ArrayList<>();
    private final List<Action> pendingActions = new ArrayList<>();
    private Map<String, DecisionTable> enabledDecisionTables = new HashMap<>();
    private final Map<String, DTDevice> availableDeviceDrivers = DTClassDiscoveryUtil.getDriverInstances();

    public DTProcessor() {}

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop){
        INSTANCE.enabledDecisionTables = DTFileDiscovery.getEnabledDecisionTables(context);
    }

    @OpModeRegistrar
    public static void registerOpModes(AnnotatedOpModeManager opModeManager) {
        for (Map.Entry<String, DecisionTable> entry : INSTANCE.enabledDecisionTables.entrySet()) {
            String tableName = entry.getKey();
            OpModeMeta.Flavor opmodeFlavor = entry.getValue().getFlavor();

            try {
                OpMode opMode = new OpMode() {
                    @Override
                    public void init() {
                        INSTANCE.initializeDiscoveredTable(this, tableName);
                        telemetry.addData("Status: ", tableName + " has been initialized");
                    }

                    @Override
                    public void loop() {
                        telemetry.addData("Status: ", tableName + " is running");
                        telemetry.addData("Runtime: ", getRuntime());
                        INSTANCE.update();
                    }
                };
                opModeManager.register(
                        new OpModeMeta.Builder()
                                .setName(tableName + " [DT]")
                                .setFlavor(opmodeFlavor)
                                .setGroup("DecisionTable")
                                .setSource(OpModeMeta.Source.EXTERNAL_LIBRARY)
                                .build(),
                        opMode);
            } catch (Exception e) {
            }
        }
    }

    private void initializeDiscoveredTable(OpMode opMode, String tableName) {
        try {
            DecisionTable decisionTable = enabledDecisionTables.get(tableName);

            if (decisionTable == null) {
                throw new RuntimeException("Decision table not found: " + tableName);
            }

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(decisionTable.getFile());
            document.getDocumentElement().normalize();

            NodeList deviceNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            Map<String, DTDevice> deviceInstances = xmlProcessor.processDevices(deviceNodes, opMode, availableDeviceDrivers);

            NodeList rulesElement = document.getElementsByTagName("Rules");
            for (int i = 0; i < rulesElement.getLength(); i++) {
                loadedRules.addAll(xmlProcessor.processRules(rulesElement.item(i).getChildNodes(), deviceInstances));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + tableName, e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Error parsing XML configuration: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing decision table from configuration: " + e.getMessage(), e);
        }
    }

    private void update() {
        try {
            pendingActions.clear();

            for (Rule rule : loadedRules) {
                boolean allConditionsMet = true;

                for (Condition condition : rule.getConditions()) {
                    if (!condition.evaluate()) {
                        allConditionsMet = false;
                        break;
                    }
                }

                if (allConditionsMet) {
                    pendingActions.addAll(rule.getActions());
                }
            }

            for (Action action : pendingActions) {
                try {
                    action.execute();
                } catch (Exception e) {
                    throw new RuntimeException("Error executing action: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during update cycle: " + e.getMessage(), e);
        }
    }

    public static DTProcessor getInstance() {
        return INSTANCE;
    }
}