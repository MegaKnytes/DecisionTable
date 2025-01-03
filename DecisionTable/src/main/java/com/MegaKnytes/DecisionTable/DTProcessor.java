package com.MegaKnytes.DecisionTable;

import com.MegaKnytes.DecisionTable.drivers.DTDriverRegistry;
import com.MegaKnytes.DecisionTable.drivers.DTPDriver;
import com.MegaKnytes.DecisionTable.utils.ConfigurationException;
import com.MegaKnytes.DecisionTable.utils.xml.Action;
import com.MegaKnytes.DecisionTable.utils.xml.Condition;
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

/**
 * DTProcessor is responsible for setting up and evaluating decision tables given a decision table file
 */
public class DTProcessor {
    // A map of driver class names to their corresponding Class objects
    private final HashMap<String, Class<? extends DTPDriver>> driverClassList;
    // A list of device lists containing initialized devices with their drivers and configuration options
    private final List<Map<String, HashMap<DTPDriver, HashMap<String, Object>>>> deviceLists = new ArrayList<>();
    // A list of rule sets containing rules with conditions and actions
    private final List<List<Rule>> ruleSets = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());
    private final XMLProcessor xmlProcessor = new XMLProcessor();
    private final OpMode opMode;

    /**
     * Constructs a DTProcessor
     *
     * @param opMode The currently running OpMode
     */
    public DTProcessor(OpMode opMode) {
        this.opMode = opMode;
        // Initialize the list of driver classes that implement the DTPDriver interface
        this.driverClassList = DTDriverRegistry.getClassesWithInstanceOf(opMode.hardwareMap.appContext);
    }


    /**
     * Loads a decision table file and initializes devices and rules
     *
     * @param file The decision table file to be loaded
     * @throws ConfigurationException if an error is found during file parsing or device initialization
     */
    public void loadFile(File file) {
        try {
            // Open and Parse the XML file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();

            // Get the nodes for devices and rules
            NodeList deviceInitNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            NodeList ruleNodes = document.getElementsByTagName("Rules").item(0).getChildNodes();

            // Process the XML nodes and initialize devices and rules
            HashMap<String, HashMap<DTPDriver, HashMap<String, Object>>> deviceList = xmlProcessor.processXMLDevices(deviceInitNodes, driverClassList);
            List<Rule> rules = xmlProcessor.processXMLRules(ruleNodes, deviceList);

            // Add the device list and rule set to the respective lists
            deviceLists.add(deviceList);
            ruleSets.add(rules);

            // Process Device List & Initialize Drivers
            for (String deviceName : deviceList.keySet()) {
                try {
                    // Get the driver and configuration options for the device and set it up
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

    /**
     * Evaluates the decision tables and executes actions if conditions are met
     */
    public void evaluate() {
        // Iterate through the rule sets and evaluate each rule
        for (int i = 0; i < ruleSets.size(); i++) {
            // Get the rules and device list for the current rule set
            List<Rule> rules = ruleSets.get(i);
            Map<String, HashMap<DTPDriver, HashMap<String, Object>>> deviceList = deviceLists.get(i);

            for (Rule rule : rules) {
                // Evaluate each rule
                LOGGER.info("Evaluating rule: " + rule.getDescription());
                boolean conditionsMet = true;
                for (Condition condition : rule.getConditions()) {
                    // Get the current value of the property and evaluate the condition
                    DTPDriver driver = deviceList.get(condition.getDevice()).entrySet().iterator().next().getKey();
                    Object currentValue = driver.get(condition.getProperty());
                    LOGGER.info("Evaluating condition: " + condition.getDevice() + " " + condition.getProperty() + " " + condition.getComparison() + " " + condition.getValue());
                    // If the condition is not met, break out of the loop
                    if (!evaluateComparison(currentValue, condition.getComparison(), condition.getValue())) {
                        conditionsMet = false;
                        LOGGER.info("Condition not met: " + condition.getDevice() + " " + condition.getProperty());
                        break;
                    }
                }
                // If all conditions are met, execute the actions in the rule
                if (conditionsMet) {
                    LOGGER.info("All conditions met for rule: " + rule.getDescription());
                    for (Action action : rule.getActions()) {
                        // Execute the action by setting the property in the driver to the specified value
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

    /**
     * Evaluates a comparison between the current value and the expected value
     *
     * @param currentValue The current value of the property
     * @param comparison   The comparison operator (e.g., "GREATER_THAN", "LESS_THAN", "EQUAL", "BOOLEAN")
     * @param value        The expected value to compare against
     * @return true if the comparison is satisfied, false otherwise
     * @throws ConfigurationException if an invalid comparison operator is provided
     */
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

    /**
     * Retrieves a file from the asset folder
     *
     * @param fileName The name of the file to be retrieved
     * @return The retrieved file
     * @throws ConfigurationException if an error occurs while reading the file
     */
    public File getFileFromAssetFolder(String fileName) {
        try {
            // Read the file from the asset folder and create a temporary file
            InputStream inputStream = opMode.hardwareMap.appContext.getAssets().open(fileName);
            File tempFile = File.createTempFile(fileName, null);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            // Write the file to the temporary file
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