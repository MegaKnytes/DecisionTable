package com.MegaKnytes.DecisionTable;

import com.MegaKnytes.DecisionTable.drivers.DTDriverRegistry;
import com.MegaKnytes.DecisionTable.drivers.DTPDriver;
import com.MegaKnytes.DecisionTable.utils.xml.Action;
import com.MegaKnytes.DecisionTable.utils.xml.Condition;
import com.MegaKnytes.DecisionTable.utils.ConfigurationException;
import com.MegaKnytes.DecisionTable.utils.xml.Rule;
import com.MegaKnytes.DecisionTable.utils.xml.XMLProcessor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DTProcessor {
    private HashMap<String, Class<? extends DTPDriver>> driverClassList;
    private List<Map<String, HashMap<DTPDriver, HashMap<String, Object>>>> deviceLists = new ArrayList<>();
    private List<List<Rule>> ruleSets = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());
    private final XMLProcessor xmlProcessor = new XMLProcessor();
    private final OpMode opMode;

    public DTProcessor(OpMode opMode) {
        this.opMode = opMode;
    }

    public void setup() {
        driverClassList = DTDriverRegistry.getClassesWithInstanceOf(opMode.hardwareMap.appContext);
    }

    public void loadFile(File file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();

            NodeList deviceInitNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            NodeList ruleNodes = document.getElementsByTagName("Rules").item(0).getChildNodes();

            HashMap<String, HashMap<DTPDriver, HashMap<String, Object>>> deviceList = xmlProcessor.processXMLDevices(deviceInitNodes, driverClassList);
            List<Rule> rules = xmlProcessor.processXMLRules(ruleNodes, deviceList);

            deviceLists.add(deviceList);
            ruleSets.add(rules);

            // Process Device List & Initialize Drivers
            for (String deviceName : deviceList.keySet()) {
                try {
                    DTPDriver deviceDriver = Objects.requireNonNull(deviceList.get(deviceName)).entrySet().iterator().next().getKey();
                    HashMap<String, Object> config = Objects.requireNonNull(deviceList.get(deviceName)).entrySet().iterator().next().getValue();
                    deviceDriver.setup(opMode, deviceName, config);
                } catch (Exception e) {
                    throw new ConfigurationException("An error occurred: " + e);
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException(e.toString());
        }
    }

    public void evaluate() {
        for (int i = 0; i < ruleSets.size(); i++) {
            List<Rule> rules = ruleSets.get(i);
            Map<String, HashMap<DTPDriver, HashMap<String, Object>>> deviceList = deviceLists.get(i);

            for (Rule rule : rules) {
                LOGGER.info("Evaluating rule: " + rule.getDescription());
                boolean conditionsMet = true;
                for (Condition condition : rule.getConditions()) {
                    DTPDriver driver = deviceList.get(condition.getDevice()).entrySet().iterator().next().getKey();
                    Object currentValue = driver.get(condition.getProperty());
                    LOGGER.info("Evaluating condition: " + condition.getDevice() + " " + condition.getProperty() + " " + condition.getComparison() + " " + condition.getValue());
                    if (!evaluateComparison(currentValue, condition.getComparison(), condition.getValue())) {
                        conditionsMet = false;
                        LOGGER.info("Condition not met: " + condition.getDevice() + " " + condition.getProperty());
                        break;
                    }
                }
                if (conditionsMet) {
                    LOGGER.info("All conditions met for rule: " + rule.getDescription());
                    for (Action action : rule.getActions()) {
                        DTPDriver driver = deviceList.get(action.getDevice()).entrySet().iterator().next().getKey();
                        LOGGER.info("Executing action: Setting " + action.getProperty() + " to " + action.getValue() + " in device " + action.getDevice() + " with driver " + driver.getClass().getSimpleName());
                        driver.set(action.getProperty(), action.getValue());
                    }
                } else {
                    LOGGER.info("Conditions not met for rule: " + rule.getDescription());
                }
            }
        }
    }

    private boolean evaluateComparison(Object currentValue, String comparison, String value) {
        switch (comparison) {
            case "GREATER_THAN":
                return Double.parseDouble(currentValue.toString()) > Double.parseDouble(value);
            case "LESS_THAN":
                return Double.parseDouble(currentValue.toString()) < Double.parseDouble(value);
            case "EQUAL":
                return Double.parseDouble(currentValue.toString()) == Double.parseDouble(value);
            case "BOOLEAN":
                return Boolean.parseBoolean(currentValue.toString()) == Boolean.parseBoolean(value);
            default:
                throw new ConfigurationException("Invalid comparison operator: " + comparison);
        }
    }

    public File getFileFromWebServer(String fileName) {
        File file = opMode.hardwareMap.appContext.getFileStreamPath(fileName);
        if (file.exists()) {
            return file;
        } else {
            throw new RuntimeException("File " + fileName + " not found in app context.");
        }
    }

    public File getFileFromAssetFolder(String fileName){
        try {
            InputStream inputStream = opMode.hardwareMap.appContext.getAssets().open(fileName);
            File tempFile = File.createTempFile(fileName, null);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new ConfigurationException("Error Reading File: " + e);
        }
    }
}