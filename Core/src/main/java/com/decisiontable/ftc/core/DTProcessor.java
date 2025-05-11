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

/**
 * The `DTProcessor` class contains the main interface for processing decision tables in FTC.
 * It handles the loading of configuration files, setting up devices, and the evaluation of
 * rules and actions.
 */
public class DTProcessor {
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());

    /**
     * OpMode instance provided on construction
     */
    private final OpMode opMode;

    /**
     * XMLProcessor instance for processing XML configurations
     */
    private final XMLProcessor xmlProcessor;

    /**
     * Map of available drivers
     */
    private final Map<String, Class<? extends DTDevice>> availableDrivers;

    /**
     * List of rules to be evaluated
     */
    private final List<Rule> rules = new ArrayList<>();

    /**
     * List of pending actions to be executed
     */
    private final List<Action> pendingActions = new ArrayList<>();

    /**
     * Constructs a new instance of a Decision Table Processor.
     *
     * @param opMode The OpMode instance provided by the user, used to access the hardware map and execute device commands.
     */
    public DTProcessor(OpMode opMode) {
        this.opMode = opMode;
        this.xmlProcessor = new XMLProcessor();

        // Initialize available drivers using the DTDriverRegistry
        availableDrivers = DTDriverRegistry.getClassesWithInstanceOf(opMode.hardwareMap.appContext);
    }

    /**
     * Loads the configuration from an XML file located in the application assets folder ["FtcRobotController/src/main/assets"].
     * <p>
     * This method parses the XML file, processes the device drivers, and sets up and initializes the devices.
     *
     * @param assetPath The relative path to the XML file in the application assets folder (e.g., "IntoTheDeep.xml").
     * @throws RuntimeException if an error occurs while parsing the XML file or setting up the devices.
     */
    public void loadConfigurationFromAsset(String assetPath) {
        // Attempt to load the file with the given asset path from the application assets folder using the OpMode's context
        try (InputStream inputStream = opMode.hardwareMap.appContext.getAssets().open(assetPath)) {
            // Initialize the XML parser and parse the input stream
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Process the XML devices nodes and initialize the device driver instances list
            NodeList deviceNodes = document.getElementsByTagName("Devices").item(0).getChildNodes();
            Map<String, DTDevice> deviceDriverInstances = xmlProcessor.processDeviceDrivers(deviceNodes, availableDrivers);

            // Process the XML rules nodes and add all of them to the rules list
            NodeList rulesElement = document.getElementsByTagName("Rules");
            for (int i = 0; i < rulesElement.getLength(); i++) {
                rules.addAll(xmlProcessor.processRules(rulesElement.item(i).getChildNodes(), deviceDriverInstances));
            }

            // Setup each device driver instance and initialize the device according to the DTDevice implementation
            for (Map.Entry<String, DTDevice> entry : deviceDriverInstances.entrySet()) {
                String deviceName = entry.getKey();     // Get the device name from the entry
                DTDevice device = entry.getValue();     // Get the device driver instance from the entry
                try {
                    device.setup(opMode, deviceName);   // Setup the device driver instance
                } catch (Exception e) {
                    // Something went wrong during the setup of the device driver instance, so we should alert the user
                    throw new RuntimeException("Failed to setup device: " + deviceName, e);
                }
            }

            LOGGER.log(Level.INFO, "Configuration loaded successfully");
        } catch (IOException e) {
            throw new RuntimeException( "Failed to load configuration from asset: " + assetPath + ". Does the file exist?", e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluates all of the rules and executes the actions of the rules that have all of their conditions met.
     * <p>
     * This method should be called repeatedly throughout the life of the OpMode's main loop to update the state of the decision table.
     *
     * @throws RuntimeException if an error occurs while executing an action.
     */
    public void update() {
        // Iterate through all loaded rules
        for (Rule rule : rules) {
            boolean allConditionsMet = true;

            // Check if all conditions of the rule are met
            for (Condition condition : rule.getConditions()) {
                if (!condition.evaluate()) {
                    allConditionsMet = false;
                    break;
                }
            }

            // If all conditions are met, add the actions to the pending actions list
            if (allConditionsMet) {
                pendingActions.addAll(rule.getActions());
            }
        }

        // Execute all pending actions
        for (Action action : pendingActions) {
            try {
                action.execute();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error executing action: " + e.getMessage(), e);
                throw new RuntimeException("Error executing action: " + e.getMessage());
            }
        }

        // Clear the pending actions list after executing all actions for the next update
        pendingActions.clear();
    }
}