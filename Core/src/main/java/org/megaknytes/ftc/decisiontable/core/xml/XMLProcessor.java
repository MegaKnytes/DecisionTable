package org.megaknytes.ftc.decisiontable.core.xml;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLUtils;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Rule;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueParser;
import org.megaknytes.ftc.decisiontable.core.xml.values.valuetypes.ParameterValue;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLProcessor {
    private final ParameterRegistry parameterRegistry = ParameterRegistry.getInstance();

    public Map<String, DTDevice> processDevices(NodeList deviceNodes, OpMode opMode, Map<String, DTDevice> availableDeviceDrivers) {
        Map<String, DTDevice> instances = new HashMap<>();

        for (int deviceCount = 0; deviceCount < deviceNodes.getLength(); deviceCount++) {
            Node node = deviceNodes.item(deviceCount);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element deviceElement = (Element) node;
            String deviceName = deviceElement.getNodeName();

            Element driverElement = XMLUtils.getFirstChildElement(deviceElement);
            if (driverElement == null) {
                throw new ConfigurationException("Driver element missing for device: " + deviceName);
            }

            String driverType = driverElement.getNodeName();

            if (!availableDeviceDrivers.containsKey(driverType)) {
                throw new DriverNotFoundException("Driver with name " + driverType + " not found, has it been enabled?");
            }

            if (instances.containsKey(deviceName)) {
                throw new ConfigurationException("Duplicate device name: " + deviceName);
            }

            try {
                DTDevice driver = availableDeviceDrivers.get(driverType);
                if (driver == null) {
                    throw new RuntimeException("Error creating driver instance for " + driverType);
                }
                Class<?> driverClass = driver.getClass();
                DTDevice driverInstance = (DTDevice) driverClass.newInstance();
                instances.put(deviceName, driverInstance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create device " + deviceName, e);
            }
        }

        ParameterValue.setDeviceInstances(instances);

        for (int deviceCount = 0; deviceCount < deviceNodes.getLength(); deviceCount++) {
            Node node = deviceNodes.item(deviceCount);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;

            Element deviceElement = (Element) node;
            String deviceName = deviceElement.getNodeName();
            Element driverElement = XMLUtils.getFirstChildElement(deviceElement);

            if (driverElement != null) {
                DTDevice device = instances.get(deviceName);

                if (device == null) {
                    throw new RuntimeException("Device " + deviceName + " not found in instances, has it been initialized?");
                }

                device.registerParameters(opMode, parameterRegistry);

                NodeList paramNodes = driverElement.getChildNodes();

                for (int parameterCount = 0; parameterCount < paramNodes.getLength(); parameterCount++) {
                    Node paramNode = paramNodes.item(parameterCount);
                    if (paramNode.getNodeType() != Node.ELEMENT_NODE) continue;

                    String paramName = paramNode.getNodeName();

                    try {
                        Parameter<?> parameter = parameterRegistry.getParameter(device, paramName);

                        if (parameter == null) {
                            throw new ConfigurationException("Parameter " + paramName + " not found in device " + deviceName);
                        }

                        Value<?> value = ValueParser.parseValue(paramNode, parameter.getType());

                        new Action(parameter, value).execute();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize parameter " + paramName + " for device " + deviceName);
                    }
                }
            }
        }

        return instances;
    }

    public List<Rule> processRules(NodeList ruleNodes, Map<String, DTDevice> availableDeviceDrivers) {
        List<Rule> rules = new ArrayList<>();

        for (int ruleCount = 0; ruleCount < ruleNodes.getLength(); ruleCount++) {
            Node ruleNode = ruleNodes.item(ruleCount);
            if (ruleNode.getNodeType() != Node.ELEMENT_NODE) continue;

            Element ruleElement = (Element) ruleNode;
            if (!"Rule".equals(ruleElement.getNodeName())) continue;

            String description = ruleElement.getAttribute("description");
            List<Condition> conditions = processConditions(ruleElement, availableDeviceDrivers);
            List<Action> actions = processActions(ruleElement, availableDeviceDrivers);

            rules.add(new Rule(description, conditions, actions));
        }

        return rules;
    }

    private List<Condition> processConditions(Element ruleElement, Map<String, DTDevice> availableDeviceDrivers) {
        List<Condition> conditions = new ArrayList<>();
        NodeList conditionNodes = ruleElement.getElementsByTagName("Condition");

        for (int conditionCount = 0; conditionCount < conditionNodes.getLength(); conditionCount++) {
            Element conditionElement = (Element) conditionNodes.item(conditionCount);
            NodeList deviceNodes = conditionElement.getChildNodes();

            for (int deviceCount = 0; deviceCount < deviceNodes.getLength(); deviceCount++) {
                Node deviceNode = deviceNodes.item(deviceCount);
                if (deviceNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element deviceElement = (Element) deviceNode;
                String deviceName = deviceElement.getNodeName();
                DTDevice device = availableDeviceDrivers.get(deviceName);

                if (device == null) {
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                NodeList paramNodes = deviceElement.getChildNodes();
                for (int parameterCount = 0; parameterCount < paramNodes.getLength(); parameterCount++) {
                    Node paramNode = paramNodes.item(parameterCount);
                    if (paramNode.getNodeType() != Node.ELEMENT_NODE) continue;

                    Element paramElement = (Element) paramNode;
                    String paramName = paramElement.getNodeName();
                    String operator = paramElement.getAttribute("operator");
                    if (operator.isEmpty()) operator = "==";

                    Parameter<?> parameter = parameterRegistry.getParameter(device, paramName);

                    if (parameter == null) {
                        throw new IllegalParameterException("Parameter not found: " + deviceName + "." + paramName);
                    }

                    Value<?> expectedValue = ValueParser.parseValue(paramElement, parameter.getType());

                    conditions.add(new Condition(parameter, operator, expectedValue));
                }
            }
        }

        return conditions;
    }

    private List<Action> processActions(Element ruleElement, Map<String, DTDevice> availableDeviceDrivers) {
        List<Action> actions = new ArrayList<>();
        NodeList actionNodes = ruleElement.getElementsByTagName("Action");

        for (int actionCount = 0; actionCount < actionNodes.getLength(); actionCount++) {
            Element actionElement = (Element) actionNodes.item(actionCount);

            NodeList deviceNodes = actionElement.getChildNodes();
            for (int deviceCount = 0; deviceCount < deviceNodes.getLength(); deviceCount++) {
                Node deviceNode = deviceNodes.item(deviceCount);
                if (deviceNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element deviceElement = (Element) deviceNode;
                String deviceName = deviceElement.getNodeName();
                DTDevice device = availableDeviceDrivers.get(deviceName);

                if (device == null) {
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                NodeList paramNodes = deviceElement.getChildNodes();
                for (int parameterCount = 0; parameterCount < paramNodes.getLength(); parameterCount++) {
                    Node paramNode = paramNodes.item(parameterCount);
                    if (paramNode.getNodeType() != Node.ELEMENT_NODE) continue;

                    Element paramElement = (Element) paramNode;
                    String paramName = paramElement.getNodeName();

                    Parameter<?> parameter = parameterRegistry.getParameter(device, paramName);

                    if (parameter == null) {
                        throw new IllegalParameterException("Parameter not found: " + deviceName + "." + paramName);
                    }

                    Value<?> value = ValueParser.parseValue(paramElement, parameter.getType());

                    actions.add(new Action(parameter, value));
                }
            }
        }

        return actions;
    }
}