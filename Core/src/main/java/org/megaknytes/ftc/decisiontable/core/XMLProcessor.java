package org.megaknytes.ftc.decisiontable.core;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.Action;
import org.megaknytes.ftc.decisiontable.core.xml.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.Rule;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.BooleanValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.DoubleValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.FloatValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.IntegerValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.ParameterValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.StringValue;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLProcessor {
    private static final Logger LOGGER = Logger.getLogger(XMLProcessor.class.getName());
    private final ParameterRegistry parameterRegistry = ParameterRegistry.getInstance();

    public Map<String, DTDevice> processDeviceDrivers(NodeList deviceNodes, Map<String, Class<? extends DTDevice>> availableDrivers) {
        Map<String, DTDevice> instances = new HashMap<>();

        for (int i = 0; i < deviceNodes.getLength(); i++) {
            Node node = deviceNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("Device")) {
                Element deviceElement = (Element) node;
                String deviceName = deviceElement.getAttribute("name");
                String driverType = deviceElement.getAttribute("driver");

                try {
                    if (!availableDrivers.containsKey(driverType)) {
                        LOGGER.log(Level.SEVERE, "Driver not found: " + driverType);
                        throw new RuntimeException("Driver not found: " + driverType);
                    }

                    Class<? extends DTDevice> driverClass = availableDrivers.get(driverType);
                    DTDevice driver = driverClass.getDeclaredConstructor().newInstance();

                    instances.put(deviceName, driver);
                    driver.registerParameters(deviceName, parameterRegistry);

                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to create driver instance: " + driverType, e);
                }
            }
        }

        return instances;
    }

    public List<Rule> processRules(NodeList rulesNodes, Map<String, DTDevice> deviceDriverInstances) {
        List<Rule> rules = new ArrayList<>();
        for (int i = 0; i < rulesNodes.getLength(); i++) {
            Node node = rulesNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("Rule")) {
                Element ruleElement = (Element) node;
                String description = ruleElement.getAttribute("description");

                NodeList conditionNodes = ruleElement.getElementsByTagName("Condition");
                List<Condition> conditions = parseConditions(conditionNodes);

                NodeList actionNodes = ruleElement.getElementsByTagName("Action");
                List<Action> actions = parseActions(actionNodes, deviceDriverInstances);

                Rule rule = new Rule(description, conditions, actions);
                rules.add(rule);
            }
        }
        return rules;
    }

    private List<Condition> parseConditions(NodeList conditionNodes) {
        List<Condition> conditions = new ArrayList<>();

        for (int i = 0; i < conditionNodes.getLength(); i++) {
            if (conditionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element conditionElement = (Element) conditionNodes.item(i);
                String deviceName = conditionElement.getAttribute("device");
                String parameterName = conditionElement.getAttribute("parameter");
                String operator = conditionElement.getAttribute("operator");
                String valueType = conditionElement.getAttribute("type");
                String valueStr = conditionElement.getAttribute("value");

                Parameter<?> parameter = parameterRegistry.getParameter(deviceName + "." + parameterName);
                if (parameter == null) {
                    LOGGER.log(Level.WARNING, "Parameter not found: " + deviceName + "." + parameterName);
                    throw new RuntimeException("Parameter not found: " + deviceName + "." + parameterName);
                }

                Value<?> value = parseValue(valueType, valueStr);
                if (value == null) continue;

                Condition condition = new Condition(parameter, operator, value);
                conditions.add(condition);
            }
        }
        return conditions;
    }

    private List<Action> parseActions(NodeList actionNodes, Map<String, DTDevice> deviceDriverInstances) {
        List<Action> actions = new ArrayList<>();

        for (int i = 0; i < actionNodes.getLength(); i++) {
            if (actionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element actionElement = (Element) actionNodes.item(i);
                String deviceName = actionElement.getAttribute("device");
                String parameterName = actionElement.getAttribute("parameter");
                String valueType = actionElement.getAttribute("type");
                String valueStr = actionElement.getAttribute("value");

                DTDevice device = deviceDriverInstances.get(deviceName);
                if (device == null) {
                    LOGGER.log(Level.WARNING, "Device not found for action: " + deviceName);
                    throw new RuntimeException("Device not found for action: " + deviceName);
                }

                Parameter<?> parameter = parameterRegistry.getParameter(deviceName + "." + parameterName);
                if (parameter == null) {
                    LOGGER.log(Level.WARNING, "Parameter not found: " + deviceName + "." + parameterName);
                    throw new RuntimeException("Parameter not found: " + deviceName + "." + parameterName);
                }

                Value<?> value = parseValue(valueType, valueStr);
                if (value == null) continue;

                Action action = new Action(parameter, value);
                actions.add(action);
            }
        }
        return actions;
    }

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
                    LOGGER.log(Level.WARNING, "Unknown value type: " + valueType);
                    throw new RuntimeException("Unknown value type: " + valueType);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error parsing value: " + valueStr, e);
            throw new RuntimeException("Error parsing value: " + valueStr, e);
        }
    }
}