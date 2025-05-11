package com.decisiontable.ftc.core;

import com.decisiontable.ftc.core.drivers.DTDevice;
import com.decisiontable.ftc.core.xml.Action;
import com.decisiontable.ftc.core.xml.Condition;
import com.decisiontable.ftc.core.xml.Rule;
import com.decisiontable.ftc.core.xml.parameters.Parameter;
import com.decisiontable.ftc.core.xml.parameters.ParameterRegistry;
import com.decisiontable.ftc.core.xml.values.Value;
import com.decisiontable.ftc.core.xml.values.types.BooleanValue;
import com.decisiontable.ftc.core.xml.values.types.DoubleValue;
import com.decisiontable.ftc.core.xml.values.types.FloatValue;
import com.decisiontable.ftc.core.xml.values.types.IntegerValue;
import com.decisiontable.ftc.core.xml.values.types.ParameterValue;
import com.decisiontable.ftc.core.xml.values.types.StringValue;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The `XMLProcessor` class is responsible for processing user supplied XML configuration files.
 * <p>
 * It provides methods to parse device drivers, rules, conditions, and actions
 * from XML nodes, enabling the setup and execution of decision tables.
 */
public class XMLProcessor {
    private static final Logger LOGGER = Logger.getLogger(XMLProcessor.class.getName());
    private final ParameterRegistry parameterRegistry = ParameterRegistry.getInstance();

    /**
     * Processes the device drivers as defined in the XML configuration.
     * <p>
     * <pre>
     * {@code
     * </Devices>
     *      <Device name="{device-name}" driver="{device-driver}" />
     *  </Devices>
     * }
     * </pre>
     *
     * @param deviceNodes      The NodeList containing device driver nodes from the XML configuration.
     * @param availableDrivers A map of available drivers, where the key is the driver type and the value is the driver class.
     * @throws RuntimeException if a driver is not found or an error occurs while creating a driver instance.
     * @return A map of device names to their corresponding driver instances.
     */
    public Map<String, DTDevice> processDeviceDrivers(NodeList deviceNodes, Map<String, Class<? extends DTDevice>> availableDrivers) {
        // Initialize a map to hold the device driver instances
        Map<String, DTDevice> instances = new HashMap<>();

        // Iterate through the device nodes in the XML configuration
        for (int i = 0; i < deviceNodes.getLength(); i++) {
            Node node = deviceNodes.item(i);

            // Check if the node is an element and has the name "Device"
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("Device")) {
                // Cast the node to an Element and retrieve its attributes
                Element deviceElement = (Element) node;
                String deviceName = deviceElement.getAttribute("name");
                String driverType = deviceElement.getAttribute("driver");

                try {
                    // Check if the driver is available
                    if (!availableDrivers.containsKey(driverType)) {
                        LOGGER.log(Level.SEVERE, "Driver not found: " + driverType);
                        throw new RuntimeException("Driver not found: " + driverType);
                    }

                    // Create a new instance of the driver class using reflection
                    Class<? extends DTDevice> driverClass = availableDrivers.get(driverType);
                    assert driverClass != null;
                    DTDevice driver = driverClass.getDeclaredConstructor().newInstance();

                    // Put the driver instance in the map and register its parameters
                    instances.put(deviceName, driver);
                    driver.registerParameters(deviceName, parameterRegistry);

                } catch (Exception e) {
                    // Throw a runtime exception if an error occurs while creating the driver instance
                    throw new RuntimeException( "Failed to create driver instance: " + driverType, e);
                }
            }
        }

        return instances;
    }

    /**
     * Process the rules as defined in the XML configuration.
     * <p>
     * This method parses the rules and their conditions and actions from the given XML.
     * Each rule is represented by a `Rule` object, which contains a description, a list of conditions,
     * and a list of actions. The method call returns a list of all the rules found in the XML.
     *
     * @param rulesNodes            The NodeList containing rule nodes from the XML configuration.
     * @param deviceDriverInstances A Map of device names to their corresponding driver instances.
     * @return A List of `Rule` objects representing the rules defined in the XML.
     * @throws RuntimeException if a parameter or device is not found, or if an error occurs while parsing the XML.
     */
    public List<Rule> processRules(NodeList rulesNodes, Map<String, DTDevice> deviceDriverInstances) {
        // Initialize a list to hold the rules
        List<Rule> rules = new ArrayList<>();

        // Iterate through the rule nodes in the XML configuration
        for (int i = 0; i < rulesNodes.getLength(); i++) {
            // Check if the node is an element and has the name "Rule"
            Node node = rulesNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("Rule")) {
                // Cast the node to an Element and retrieve its attributes
                Element ruleElement = (Element) node;
                String description = ruleElement.getAttribute("description");

                // Parse the conditions and actions for the rule
                NodeList conditionNodes = ruleElement.getElementsByTagName("Condition");
                List<Condition> conditions = parseConditions(conditionNodes);

                NodeList actionNodes = ruleElement.getElementsByTagName("Action");
                List<Action> actions = parseActions(actionNodes, deviceDriverInstances);

                // Construct a new Rule object and add it to the list
                Rule rule = new Rule(description, conditions, actions);
                rules.add(rule);
            }
        }

        // Return the list of processed rules
        return rules;
    }

    /**
     * Process the conditions as defined in the XML configuration of each rule.
     * <p>
     * This method parses the conditions from the given XML nodes and returns a list of `Condition` objects.
     * Each condition is represented by a `Condition` object, which contains a parameter, an operator, and a value.
     * The method call returns a list of all the conditions found in the XML.
     *
     * @param conditionNodes The NodeList containing condition nodes from the XML configuration.
     * @return A list of `Condition` objects representing the conditions defined in the XML.
     * @throws RuntimeException if a parameter is not found, or if an error occurs while parsing the XML.
     */
    private List<Condition> parseConditions(NodeList conditionNodes) {
        // Initialize a list to hold the discovered conditions
        List<Condition> conditions = new ArrayList<>();

        // Iterate through the condition nodes in the XML configuration
        for (int i = 0; i < conditionNodes.getLength(); i++) {
            if (conditionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                // Cast the node to an Element and retrieve its attributes
                Element conditionElement = (Element) conditionNodes.item(i);
                String deviceName = conditionElement.getAttribute("device");
                String parameterName = conditionElement.getAttribute("parameter");
                String operator = conditionElement.getAttribute("operator");
                String valueType = conditionElement.getAttribute("type");
                String valueStr = conditionElement.getAttribute("value");

                // Retrieve the device driver instance from the map
                Parameter<?> parameter = parameterRegistry.getParameter(deviceName + "." + parameterName);

                // Check if the device driver instance is available and if the parameter is registered
                if (parameter == null) {
                    // Throw a runtime exception if the parameter is not found
                    throw new RuntimeException("Parameter not found: " + deviceName + "." + parameterName);
                }

                // Parse the value based on its type
                Value<?> value = parseValue(valueType, valueStr);
                if (value == null) continue;

                // Create a new Condition object and add it to the list
                Condition condition = new Condition(parameter, operator, value);
                conditions.add(condition);
            }
        }

        // Return the list of processed conditions
        return conditions;
    }

    /**
     * Process the actions as defined in the XML configuration of each rule.
     * <p>
     * This method parses the actions from the given XML nodes and returns a list of `Action` objects.
     * Each action is represented by an `Action` object, which contains a parameter and a value.
     * The method call returns a list of all the actions found in the XML.
     *
     * @param actionNodes           The NodeList containing action nodes from the XML configuration.
     * @param deviceDriverInstances A Map of device names to their corresponding driver instances.
     * @return A list of `Action` objects representing the actions defined in the XML.
     * @throws RuntimeException if a parameter or device is not found, or if an error occurs while parsing the XML.
     */
    private List<Action> parseActions(NodeList actionNodes, Map<String, DTDevice> deviceDriverInstances) {
        // Initialize a list to hold the discovered actions
        List<Action> actions = new ArrayList<>();

        // Iterate through the action nodes in the XML configuration
        for (int i = 0; i < actionNodes.getLength(); i++) {
            if (actionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                // Cast the node to an Element and retrieve its attributes
                Element actionElement = (Element) actionNodes.item(i);
                String deviceName = actionElement.getAttribute("device");
                String parameterName = actionElement.getAttribute("parameter");
                String valueType = actionElement.getAttribute("type");
                String valueStr = actionElement.getAttribute("value");

                // Retrieve the device driver instance from the map
                DTDevice device = deviceDriverInstances.get(deviceName);
                if (device == null) {
                    // Throw a runtime exception if the device is not found
                    throw new RuntimeException("Device not found for action: " + deviceName);
                }

                // Check if the parameter is registered in the parameter registry
                Parameter<?> parameter = parameterRegistry.getParameter(deviceName + "." + parameterName);
                if (parameter == null) {
                    // Throw a runtime exception if the parameter is not found
                    throw new RuntimeException("Parameter not found: " + deviceName + "." + parameterName);
                }

                // Parse the value based on its type
                Value<?> value = parseValue(valueType, valueStr);
                if (value == null) continue;

                // Create a new Action object and add it to the list
                Action action = new Action(parameter, value);
                actions.add(action);
            }
        }

        // Return the list of processed actions
        return actions;
    }

    /**
     * Parses a value based on its type and string representation.
     * <p>
     * This method converts the string representation of a value into its corresponding type.
     *
     * @param valueType The type of the value
     * @param valueStr  The string representation of the value to be parsed.
     * @return A `Value<?>` object representing the parsed value.
     * @throws RuntimeException if the value type is unknown or if an error occurs while parsing the value.`
     */
    private Value<?> parseValue(String valueType, String valueStr) {
        try {
            switch (valueType.toLowerCase()) {
                case "boolean":
                    return new BooleanValue(Boolean.parseBoolean(valueStr));
                case "integer":
                    return new IntegerValue(Integer.parseInt(valueStr));
                case "float":
                    return new FloatValue(Float.parseFloat(valueStr));
                case "double":
                    return new DoubleValue(Double.parseDouble(valueStr));
                case "string":
                    return new StringValue(valueStr);
                case "parameter":
                    String[] parts = valueStr.split("\\.", 2);
                    return new ParameterValue(parts[0], parts[1]);
                default:
                    throw new RuntimeException("Unknown value type: " + valueType);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing value: " + valueStr, e);
        }
    }
}