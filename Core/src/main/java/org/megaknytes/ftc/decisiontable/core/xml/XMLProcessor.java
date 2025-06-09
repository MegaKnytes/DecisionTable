package org.megaknytes.ftc.decisiontable.core.xml;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLUtils;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterGroup;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
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

public class XMLProcessor {
    private final ParameterRegistry parameterRegistry = ParameterRegistry.getInstance();

    public Map<String, DTDevice> processDevices(NodeList elementNodes, OpMode opMode, Map<String, DTDevice> availableDeviceDrivers) {
        Map<String, DTDevice> deviceInstances = new HashMap<>();
        List<Element> driverElements = XMLUtils.getElementNodes(elementNodes);

        for (Element driverElement : driverElements) {
            String driverName = driverElement.getNodeName();

            if (!availableDeviceDrivers.containsKey(driverName)) {
                throw new DriverNotFoundException("Driver with name " + driverName + " not found, has it been enabled?");
            }

            DTDevice driverTemplate = availableDeviceDrivers.get(driverName);
            assert driverTemplate != null;
            Class<?> driverClass = driverTemplate.getClass();

            List<Element> deviceElements = XMLUtils.getElementNodes(driverElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();

                if (deviceInstances.containsKey(deviceName)) {
                    throw new ConfigurationException("Duplicate device name: " + deviceName);
                }

                try {
                    DTDevice deviceInstance = (DTDevice) driverClass.newInstance();
                    deviceInstances.put(deviceName, deviceInstance);

                    deviceInstance.registerParameters(opMode, parameterRegistry);

                    List<Element> groupElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                    for (Element groupElement : groupElements) {
                        String groupName = groupElement.getNodeName();
                        ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

                        if (group == null) {
                            throw new ConfigurationException("Group " + groupName + " not found in device " + deviceName);
                        }

                        List<Element> paramElements = XMLUtils.getElementNodes(groupElement.getChildNodes());

                        for (Element paramElement : paramElements) {
                            String parameterName = paramElement.getNodeName();
                            Parameter<?> parameter = group.getParameter(parameterName);

                            if (parameter == null) {
                                throw new ConfigurationException("Parameter " + parameterName + " not found in group " + groupName);
                            }

                            Value<?> value = ValueParser.parseValue(paramElement, parameter.getType());
                            new Action(parameter, value).execute();
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to create device " + deviceName, e);
                }
            }
        }

        ParameterValue.setDeviceInstances(deviceInstances);
        return deviceInstances;
    }

    public List<Rule> processRules(NodeList elementNodes, Map<String, DTDevice> availableDeviceDrivers) {
        List<Rule> rules = new ArrayList<>();
        List<Element> ruleElements = XMLUtils.getElementNodes(elementNodes);

        for (Element ruleElement : ruleElements) {
            String description = ruleElement.getAttribute("description");
            List<Condition> conditions = processConditions(ruleElement, availableDeviceDrivers);
            List<Action> actions = processActions(ruleElement, availableDeviceDrivers);

            rules.add(new Rule(description, conditions, actions));
        }

        return rules;
    }

    private List<Condition> processConditions(Element element, Map<String, DTDevice> availableDeviceDrivers) {
        List<Condition> conditions = new ArrayList<>();
        NodeList conditionNodes = element.getElementsByTagName("Condition");

        for (int conditionCount = 0; conditionCount < conditionNodes.getLength(); conditionCount++) {
            Element conditionElement = (Element) conditionNodes.item(conditionCount);
            List<Element> deviceElements = XMLUtils.getElementNodes(conditionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> groupElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                for (Element groupElement : groupElements) {
                    String groupName = groupElement.getNodeName();
                    ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

                    if (group == null) {
                        throw new IllegalParameterException("Group " + groupName + " not found in device " + deviceName);
                    }

                    List<Element> paramElements = XMLUtils.getElementNodes(groupElement.getChildNodes());

                    for (Element paramElement : paramElements) {
                        String parameterName = paramElement.getNodeName();
                        String operator = paramElement.getAttribute("operator");
                        if (operator.isEmpty()) operator = "==";

                        Parameter<?> parameter = group.getParameter(parameterName);

                        if (parameter == null) {
                            throw new IllegalParameterException("Parameter " + parameterName + " not found in group " + groupName);
                        }

                        try {
                            Value<?> expectedValue = ValueParser.parseValue(paramElement, parameter.getType());
                            conditions.add(new Condition(parameter, operator, expectedValue));
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException("Failed to parse value for parameter " + parameterName + " in group " + groupName, e);
                        }
                    }
                }
            }
        }

        return conditions;
    }

    private List<Action> processActions(Element element, Map<String, DTDevice> availableDeviceDrivers) {
        List<Action> actions = new ArrayList<>();
        NodeList actionNodes = element.getElementsByTagName("Action");

        for (int actionCount = 0; actionCount < actionNodes.getLength(); actionCount++) {
            Element actionElement = (Element) actionNodes.item(actionCount);
            List<Element> deviceElements = XMLUtils.getElementNodes(actionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> groupElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                for (Element groupElement : groupElements) {
                    String groupName = groupElement.getNodeName();
                    ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

                    if (group == null) {
                        throw new IllegalParameterException("Group " + groupName + " not found in device " + deviceName);
                    }

                    List<Element> paramElements = XMLUtils.getElementNodes(groupElement.getChildNodes());

                    for (Element paramElement : paramElements) {
                        String parameterName = paramElement.getNodeName();
                        Parameter<?> parameter = group.getParameter(parameterName);

                        if (parameter == null) {
                            throw new IllegalParameterException("Parameter " + parameterName + " not found in group " + groupName);
                        }

                        try {
                            Value<?> value = ValueParser.parseValue(paramElement, parameter.getType());
                            actions.add(new Action(parameter, value));
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException("Failed to parse value for parameter " + parameterName + " in group " + groupName, e);
                        }
                    }
                }
            }
        }

        return actions;
    }
}