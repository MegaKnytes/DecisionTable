package com.decisiontable.ftc.core;

import com.decisiontable.ftc.core.drivers.DTDriverRegistry;
import com.decisiontable.ftc.core.drivers.DTDevice;
import com.decisiontable.ftc.core.xml.Action;
import com.decisiontable.ftc.core.xml.Condition;
import com.decisiontable.ftc.core.xml.Rule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTProcessor {
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());

    private final OpMode opMode;
    private final XMLProcessor xmlProcessor;
    private final Map<String, Class<? extends DTDevice>> availableDrivers;
    private final List<Rule> rules = new ArrayList<>();
    private final List<Action> pendingActions = new ArrayList<>();

    public DTProcessor(OpMode opMode) {
        this.opMode = opMode;
        this.xmlProcessor = new XMLProcessor();
        availableDrivers = DTDriverRegistry.getClassesWithInstanceOf(opMode.hardwareMap.appContext);
    }

    public void loadConfigurationFromAsset(String assetPath) {
        try (InputStream inputStream = opMode.hardwareMap.appContext.getAssets().open(assetPath)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList deviceNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            Map<String, DTDevice> deviceDriverInstances = xmlProcessor.processDeviceDrivers(deviceNodes, availableDrivers);

            NodeList rulesElement = document.getElementsByTagName("Rules");
            for (int i = 0; i < rulesElement.getLength(); i++) {
                rules.addAll(xmlProcessor.processRules(rulesElement.item(i).getChildNodes(), deviceDriverInstances));
            }

            for (Map.Entry<String, DTDevice> entry : deviceDriverInstances.entrySet()) {
                String deviceName = entry.getKey();
                DTDevice device = entry.getValue();
                try {
                    device.setup(opMode, deviceName);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to setup device: " + deviceName, e);
                }
            }

            LOGGER.log(Level.INFO, "Configuration loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load configuration from asset: " + assetPath, e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
        for (Rule rule : rules) {
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
                LOGGER.log(Level.SEVERE, "Error executing action: " + e.getMessage(), e);
                throw new RuntimeException("Error executing action: " + e.getMessage());
            }
        }

        pendingActions.clear();
    }
}