package org.megaknytes.ftc.decisiontable.core.xml;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceExtended;
import org.megaknytes.ftc.decisiontable.core.utils.XMLUtils;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterGroup;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Rule;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueParser;
import org.megaknytes.ftc.decisiontable.core.xml.values.valuetypes.ParameterValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLProcessor {
    private static final Logger LOGGER = Logger.getLogger(XMLProcessor.class.getName());

    public static Map<String, DTDevice> processDevices(NodeList elementNodes, OpMode opMode, Map<String, DTDevice> availableDeviceDrivers, ParameterRegistry parameterRegistry) {
        LOGGER.log(Level.INFO, "Processing devices...");

        Map<String, DTDevice> deviceInstances = new HashMap<>();
        List<Element> driverElements = XMLUtils.getElementNodes(elementNodes);

        for (Element driverElement : driverElements) {
            String driverName = driverElement.getNodeName();
            LOGGER.log(Level.INFO, "Processing driver: " + driverName);

            if (!availableDeviceDrivers.containsKey(driverName)) {
                LOGGER.log(Level.SEVERE, "Driver with name " + driverName + " not found, has it been enabled?");
                throw new DriverNotFoundException("Driver with name " + driverName + " not found, has it been enabled?");
            }

            DTDevice driverTemplate = availableDeviceDrivers.get(driverName);
            assert driverTemplate != null;
            Class<?> driverClass = driverTemplate.getClass();
            Class<?> superClass = driverClass.getSuperclass();

            List<Element> deviceElements = XMLUtils.getElementNodes(driverElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();

                if (deviceInstances.containsKey(deviceName)) {
                    LOGGER.log(Level.SEVERE, "Duplicate device name found: " + deviceName);
                    throw new ConfigurationException("Duplicate device name: " + deviceName);
                }

                try {
                    DTDevice deviceInstance;
                    if (DTDeviceExtended.class.isAssignableFrom(driverClass)) {
                        deviceInstance = (DTDeviceExtended) driverClass.newInstance();
                        deviceInstances.put(deviceName, deviceInstance);

                        ((DTDeviceExtended) deviceInstance).registerConfiguration(opMode, parameterRegistry);
                    } else if (DTDevice.class.isAssignableFrom(driverClass)) {
                        deviceInstance = (DTDevice) driverClass.newInstance();
                        deviceInstances.put(deviceName, deviceInstance);

                        deviceInstance.registerConfiguration(opMode.hardwareMap, parameterRegistry);
                    } else {
                        LOGGER.log(Level.SEVERE, "Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                        throw new ConfigurationException("Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                    }

                    List<Element> groupElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                    for (Element groupElement : groupElements) {
                        String groupName = groupElement.getNodeName();
                        ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

                        if (group == null) {
                            LOGGER.log(Level.SEVERE, "Group " + groupName + " not found in device " + deviceName);
                            throw new ConfigurationException("Group " + groupName + " not found in device " + deviceName);
                        }

                        List<Element> paramElements = XMLUtils.getElementNodes(groupElement.getChildNodes());

                        for (Element paramElement : paramElements) {
                            String parameterName = paramElement.getNodeName();
                            Parameter<?> parameter = group.getParameter(parameterName);

                            if (parameter == null) {
                                LOGGER.log(Level.SEVERE, "Parameter " + parameterName + " not found in group " + groupName);
                                throw new ConfigurationException("Parameter " + parameterName + " not found in group " + groupName);
                            }

                            Value<?> value = ValueParser.parseValue(paramElement, parameter.getType());
                            new Action(parameter, value).execute();
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.log(Level.SEVERE, "Failed to instantiate device class for " + deviceName + ": " + e.getMessage(), e);
                    throw new ConfigurationException("Failed to instantiate device class for " + deviceName + ": " + e.getMessage());
                }
            }
        }

        ParameterValue.setDeviceInstances(deviceInstances);
        return deviceInstances;
    }

    public static List<Rule> processRules(NodeList elementNodes, Map<String, DTDevice> availableDeviceDrivers, ParameterRegistry parameterRegistry) {
        List<Rule> rules = new ArrayList<>();
        List<Element> ruleElements = XMLUtils.getElementNodes(elementNodes);

        for (DTDevice device : availableDeviceDrivers.values()) {
            device.registerParameters(parameterRegistry);
        }

        for (Element ruleElement : ruleElements) {
            String description = ruleElement.getAttribute("description");
            List<Condition> conditions = processConditions(ruleElement, availableDeviceDrivers, parameterRegistry);
            List<Action> actions = processActions(ruleElement, availableDeviceDrivers, parameterRegistry);

            rules.add(new Rule(description, conditions, actions));
        }

        return rules;
    }

    private static List<Condition> processConditions(Element element, Map<String, DTDevice> availableDeviceDrivers, ParameterRegistry parameterRegistry) {
        List<Condition> conditions = new ArrayList<>();
        NodeList conditionNodes = element.getElementsByTagName("Condition");

        for (int conditionCount = 0; conditionCount < conditionNodes.getLength(); conditionCount++) {
            Element conditionElement = (Element) conditionNodes.item(conditionCount);
            List<Element> deviceElements = XMLUtils.getElementNodes(conditionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    LOGGER.log(Level.SEVERE, "Device not found: " + deviceName);
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> groupElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                for (Element groupElement : groupElements) {
                    String groupName = groupElement.getNodeName();
                    ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

                    if (group == null) {
                        LOGGER.log(Level.SEVERE, "Group " + groupName + " not found in device " + deviceName);
                        throw new IllegalParameterException("Group " + groupName + " not found in device " + deviceName);
                    }

                    List<Element> paramElements = XMLUtils.getElementNodes(groupElement.getChildNodes());

                    for (Element paramElement : paramElements) {
                        String parameterName = paramElement.getNodeName();
                        String operator = paramElement.getAttribute("operator");
                        if (operator.isEmpty()) operator = "==";

                        Parameter<?> parameter = group.getParameter(parameterName);

                        if (parameter == null) {
                            LOGGER.log(Level.SEVERE, "Parameter " + parameterName + " not found in group " + groupName);
                            throw new IllegalParameterException("Parameter " + parameterName + " not found in group " + groupName);
                        }

                        try {
                            Value<?> expectedValue = ValueParser.parseValue(paramElement, parameter.getType());
                            conditions.add(new Condition(parameter, operator, expectedValue));
                        } catch (InstantiationException | IllegalAccessException e) {
                            LOGGER.log(Level.SEVERE, "Failed to parse value for parameter " + parameterName + " in group " + groupName, e);
                            throw new RuntimeException("Failed to parse value for parameter " + parameterName + " in group " + groupName, e);
                        }
                    }
                }
            }
        }

        return conditions;
    }

    private static List<Action> processActions(Element element, Map<String, DTDevice> availableDeviceDrivers, ParameterRegistry parameterRegistry) {
        List<Action> actions = new ArrayList<>();
        NodeList actionNodes = element.getElementsByTagName("Action");

        for (int actionCount = 0; actionCount < actionNodes.getLength(); actionCount++) {
            Element actionElement = (Element) actionNodes.item(actionCount);
            List<Element> deviceElements = XMLUtils.getElementNodes(actionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    LOGGER.log(Level.SEVERE, "Device not found: " + deviceName);
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> groupElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                for (Element groupElement : groupElements) {
                    String groupName = groupElement.getNodeName();
                    ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

                    if (group == null) {
                        LOGGER.log(Level.SEVERE, "Group " + groupName + " not found in device " + deviceName);
                        throw new IllegalParameterException("Group " + groupName + " not found in device " + deviceName);
                    }

                    List<Element> paramElements = XMLUtils.getElementNodes(groupElement.getChildNodes());

                    for (Element paramElement : paramElements) {
                        String parameterName = paramElement.getNodeName();
                        Parameter<?> parameter = group.getParameter(parameterName);

                        if (parameter == null) {
                            LOGGER.log(Level.SEVERE, "Parameter " + parameterName + " not found in group " + groupName);
                            throw new IllegalParameterException("Parameter " + parameterName + " not found in group " + groupName);
                        }

                        try {
                            Value<?> value = ValueParser.parseValue(paramElement, parameter.getType());
                            actions.add(new Action(parameter, value));
                        } catch (InstantiationException | IllegalAccessException e) {
                            LOGGER.log(Level.SEVERE, "Failed to parse value for parameter " + parameterName + " in group " + groupName, e);
                            throw new RuntimeException("Failed to parse value for parameter " + parameterName + " in group " + groupName, e);
                        }
                    }
                }
            }
        }

        return actions;
    }
}