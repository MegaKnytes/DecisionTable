package org.megaknytes.ftc.decisiontable.core;

import org.megaknytes.ftc.decisiontable.core.utils.DTClassDiscoveryUtil;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Rule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.xml.XMLProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTProcessor {
    private final OpMode opMode;
    private final XMLProcessor xmlProcessor;
    private final Map<String, DTDevice> availableDeviceDrivers;
    private final List<Rule> rules = new ArrayList<>();
    private final List<Action> pendingActions = new ArrayList<>();

    public DTProcessor(OpMode opMode) {
        this.opMode = opMode;
        this.xmlProcessor = new XMLProcessor();
        this.availableDeviceDrivers = DTClassDiscoveryUtil.getDriverInstances();
    }

    public void loadConfigurationFromAsset(String assetPath) {
        try (InputStream inputStream = opMode.hardwareMap.appContext.getAssets().open(assetPath)) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList deviceNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            Map<String, DTDevice> deviceInstances = xmlProcessor.processDevices(deviceNodes, opMode, availableDeviceDrivers);

            NodeList rulesElement = document.getElementsByTagName("Rules");

            for (int i = 0; i < rulesElement.getLength(); i++) {
                rules.addAll(xmlProcessor.processRules(rulesElement.item(i).getChildNodes(), deviceInstances));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from asset: " + assetPath + ". Does the file exist?", e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Error parsing XML configuration: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing decision table from configuration: " + e.getMessage(), e);
        }
    }

    public void update() {
        try {
            pendingActions.clear();

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
                    throw new RuntimeException("Error executing action: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during update cycle: " + e.getMessage(), e);
        }
    }
}