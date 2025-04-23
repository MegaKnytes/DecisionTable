package com.decisiontable.ftc.core;

import com.decisiontable.ftc.core.drivers.DTDriverRegistry;
import com.decisiontable.ftc.core.drivers.DTPDriver;
import com.decisiontable.ftc.core.utils.ConfigurationException;
import com.decisiontable.ftc.core.utils.xml.Action;
import com.decisiontable.ftc.core.utils.xml.Condition;
import com.decisiontable.ftc.core.utils.xml.Device;
import com.decisiontable.ftc.core.utils.xml.Rule;
import com.decisiontable.ftc.core.utils.xml.XMLProcessor;
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
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * DTProcessor is responsible for setting up and evaluating decision tables given a decision table file
 */
public class DTProcessor {
    private final HashMap<String, Class<? extends DTPDriver>> driverClassList;
    private HashMap<String, Device> deviceList = new HashMap<>();
    private List<Rule> ruleSets = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());
    private final XMLProcessor xmlProcessor = new XMLProcessor();
    private final OpMode opMode;

    public DTProcessor(OpMode opMode) {
        this.opMode = opMode;
        this.driverClassList = DTDriverRegistry.getClassesWithInstanceOf(opMode.hardwareMap.appContext);
    }

    public void loadFile(File file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();

            NodeList deviceInitNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            NodeList ruleNodes = document.getElementsByTagName("Rules").item(0).getChildNodes();

            deviceList = xmlProcessor.processXMLDevices(deviceInitNodes, driverClassList);
            ruleSets = xmlProcessor.processXMLRules(ruleNodes, deviceList);

            for (String deviceName : deviceList.keySet()) {
                try {
                    Device device = deviceList.get(deviceName);
                    assert device != null;
                    DTPDriver deviceDriver = device.getDriver();
                    Map<String, Object> config = device.getConfig();
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
        for (int ruleSetCount = 0; ruleSetCount < ruleSets.size(); ruleSetCount++) {
            for (Rule rule : ruleSets) {
                LOGGER.info("Evaluating rule: " + rule.getDescription());
                boolean conditionsMet = true;
                for (Condition condition : rule.getConditions()) {
                    Device device = deviceList.get(condition.getDevice());
                    assert device != null;
                    DTPDriver driver = device.getDriver();
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
                        Device device = deviceList.get(action.getDevice());
                        assert device != null;
                        DTPDriver driver = device.getDriver();
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

    public File getFileFromAssetFolder(String fileName) {
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