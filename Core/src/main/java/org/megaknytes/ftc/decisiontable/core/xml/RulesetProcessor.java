package org.megaknytes.ftc.decisiontable.core.xml;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterGroup;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Rule;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RulesetProcessor {
    private static final Logger LOGGER = Logger.getLogger(RulesetProcessor.class.getName());
    private static final ParameterRegistry PARAMETER_REGISTRY = ParameterRegistry.getInstance();

    public static List<Rule> processRules(NodeList elementNodes, Map<String, DTDevice> availableDeviceDrivers) {
        List<Rule> rules = new ArrayList<>();
        List<Element> ruleElements = XMLHelperMethods.getElementNodes(elementNodes);

        for (DTDevice device : availableDeviceDrivers.values()) {
            device.registerParameters(PARAMETER_REGISTRY);
        }

        for (Element ruleElement : ruleElements) {
            String description = ruleElement.getAttribute("description");
            List<Condition> conditions = processConditions(ruleElement, availableDeviceDrivers);
            List<Action> actions = processActions(ruleElement, availableDeviceDrivers);

            rules.add(new Rule(description, conditions, actions));
        }

        return rules;
    }

    private static List<Condition> processConditions(Element element, Map<String, DTDevice> availableDeviceDrivers) {
        List<Condition> conditions = new ArrayList<>();
        NodeList conditionNodes = element.getElementsByTagName("Condition");

        for (int conditionCount = 0; conditionCount < conditionNodes.getLength(); conditionCount++) {
            Element conditionElement = (Element) conditionNodes.item(conditionCount);
            List<Element> deviceElements = XMLHelperMethods.getElementNodes(conditionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    LOGGER.log(Level.SEVERE, "Device not found: " + deviceName);
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> groupElements = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes());

                for (Element groupElement : groupElements) {
                    String groupName = groupElement.getNodeName();
                    ParameterGroup group = PARAMETER_REGISTRY.getGroup(deviceInstance, groupName);

                    if (group == null) {
                        LOGGER.log(Level.SEVERE, "Group " + groupName + " not found in device " + deviceName);
                        throw new IllegalParameterException("Group " + groupName + " not found in device " + deviceName);
                    }

                    List<Element> paramElements = XMLHelperMethods.getElementNodes(groupElement.getChildNodes());

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

    private static List<Action> processActions(Element element, Map<String, DTDevice> availableDeviceDrivers) {
        List<Action> actions = new ArrayList<>();
        NodeList actionNodes = element.getElementsByTagName("Action");

        for (int actionCount = 0; actionCount < actionNodes.getLength(); actionCount++) {
            Element actionElement = (Element) actionNodes.item(actionCount);
            List<Element> deviceElements = XMLHelperMethods.getElementNodes(actionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    LOGGER.log(Level.SEVERE, "Device not found: " + deviceName);
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> groupElements = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes());

                for (Element groupElement : groupElements) {
                    String groupName = groupElement.getNodeName();
                    ParameterGroup group = PARAMETER_REGISTRY.getGroup(deviceInstance, groupName);

                    if (group == null) {
                        LOGGER.log(Level.SEVERE, "Group " + groupName + " not found in device " + deviceName);
                        throw new IllegalParameterException("Group " + groupName + " not found in device " + deviceName);
                    }

                    List<Element> paramElements = XMLHelperMethods.getElementNodes(groupElement.getChildNodes());

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