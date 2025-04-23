package com.decisiontable.ftc.core.utils.xml;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.decisiontable.ftc.core.drivers.DTPDriver;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLProcessor is a utility class for processing XML data related to devices and rules
 */
public class XMLProcessor {
    private static final Logger LOGGER = Logger.getLogger(XMLProcessor.class.getName());

    /**
     * Processes XML nodes representing devices and initializes them with their respective drivers
     *
     * @param deviceNodes   NodeList containing XML nodes for devices
     * @param driverClasses HashMap mapping driver names to their respective classes
     * @return HashMap containing initialized devices with their drivers and configuration options
     */
    public HashMap<String, Device> processXMLDevices(NodeList deviceNodes, HashMap<String, Class<? extends DTPDriver>> driverClasses){
        // Create a map to store the initialized devices once they have been processed
        HashMap<String, Device> initializedDevices = new HashMap<>();

        // Iterate through the device nodes
        for (int deviceNodeCount = 0; deviceNodeCount < deviceNodes.getLength(); deviceNodeCount++) {
            Node deviceNode = deviceNodes.item(deviceNodeCount);

            // Check if the node is an element node
            if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                // Cast the node to an element and get the device name
                Element deviceElement = (Element) deviceNode;
                String deviceName = deviceElement.getTagName();

                // Get the driver node from the device node
                Node driverNode = deviceElement.getFirstChild();

                // Skip any non-element nodes
                while (driverNode != null && driverNode.getNodeType() != Node.ELEMENT_NODE) {
                    driverNode = driverNode.getNextSibling();
                }

                // Check to make sure that the driver node is not null
                if (driverNode != null) {
                    // Cast the driver node to an element and get the driver name
                    Element driverElement = (Element) driverNode;
                    String driverName = driverElement.getTagName();

                    try {
                        // Try and iterate through the driver classes to find the correct driver class
                        for (Class<? extends DTPDriver> driverClass : driverClasses.values()) {
                            if (driverClass.getSimpleName().equalsIgnoreCase(driverName)) {
                                // If the driver class is found, extract the configuration options and initialize the device
                                HashMap<String, Object> deviceConfig = getDriverConfigOptions(driverElement);
                                HashMap<DTPDriver, HashMap<String, Object>> compiledDevice = new HashMap<>();
                                // Create a new instance of the driver class and add it to the compiled device
                                DTPDriver driverInstance = driverClass.asSubclass(DTPDriver.class).getDeclaredConstructor().newInstance();
                                initializedDevices.put(deviceName, new Device(driverInstance, deviceConfig));
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return initializedDevices;
    }

    /**
     * Processes XML nodes representing rules and creates a list of Rule objects
     *
     * @param ruleNodes     NodeList containing XML nodes for rules
     * @param deviceDrivers HashMap containing initialized devices with their drivers and configuration options
     * @return List of Rule objects created from the XML nodes
     */
    public List<com.decisiontable.ftc.core.utils.xml.Rule> processXMLRules(NodeList ruleNodes, HashMap<String, Device> deviceDrivers) {
        // Create a list to store the rules once they have been processed
        List<com.decisiontable.ftc.core.utils.xml.Rule> rules = new ArrayList<>();

        // Iterate through the rule nodes
        for (int ruleNodeCount = 0; ruleNodeCount < ruleNodes.getLength(); ruleNodeCount++) {
            Node ruleNode = ruleNodes.item(ruleNodeCount);

            // Check if the node is an element node
            if (ruleNode.getNodeType() == Node.ELEMENT_NODE) {
                // Cast the node to an element and get the rule description
                Element ruleElement = (Element) ruleNode;
                String description = ruleElement.getAttribute("description");

                // Get the condition and action nodes from the rule node
                NodeList conditionNodes = ruleElement.getElementsByTagName("Condition");
                List<Condition> conditions = new ArrayList<>();
                for (int conditionNodeCount = 0; conditionNodeCount < conditionNodes.getLength(); conditionNodeCount++) {
                    // Iterate through the condition nodes and extract the device, property, comparison, and value
                    Element conditionElement = (Element) conditionNodes.item(conditionNodeCount);
                    String device = conditionElement.getElementsByTagName("Device").item(0).getTextContent();
                    String property = conditionElement.getElementsByTagName("Property").item(0).getTextContent();
                    String comparison = conditionElement.getElementsByTagName("Comparison").item(0).getTextContent();
                    String value = conditionElement.getElementsByTagName("Value").item(0).getTextContent();
                    conditions.add(new Condition(device, property, comparison, value));
                }

                // Get the action nodes from the rule node
                NodeList actionNodes = ruleElement.getElementsByTagName("Action");
                List<Action> actions = new ArrayList<>();
                for (int actionNodeCount = 0; actionNodeCount < actionNodes.getLength(); actionNodeCount++) {
                    // Iterate through the action nodes and extract the device, property, and value
                    Element actionElement = (Element) actionNodes.item(actionNodeCount);
                    String device = actionElement.getElementsByTagName("Device").item(0).getTextContent();
                    String property = actionElement.getElementsByTagName("Property").item(0).getTextContent();
                    String value = actionElement.getElementsByTagName("Value").item(0).getTextContent();
                    actions.add(new Action(device, property, value));
                }

                // Add the rule to the list of rules
                rules.add(new com.decisiontable.ftc.core.utils.xml.Rule(description, conditions, actions));
            }
        }
        return rules;
    }

    /**
     * Extracts configuration options from an XML device node and returns them as a HashMap
     *
     * @param element XML element containing driver configuration options
     * @return HashMap containing driver configuration options as key-value pairs
     */
    private HashMap<String, Object> getDriverConfigOptions(Element element) {
        // Create a map to store the configuration options
        HashMap<String, Object> configOptions = new HashMap<>();
        NodeList options = element.getChildNodes();

        for (int configNodeCount = 0; configNodeCount < options.getLength(); configNodeCount++) {
            // Iterate through the child nodes of the element and extract the option name and value
            Node optionNode = options.item(configNodeCount);

            // Check if the node is an element node
            if (optionNode.getNodeType() == Node.ELEMENT_NODE) {
                // Cast the node to an element and get the option name and value
                Element optionElement = (Element) optionNode;
                String optionName = optionElement.getTagName();
                String optionValue = optionElement.getTextContent().trim();
                configOptions.put(optionName, optionValue);
            }
        }
        return configOptions;
    }
}