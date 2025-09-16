package org.megaknytes.ftc.decisiontable.core.xml.processing;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Rule;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueHandler;
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
        List<Element> conditionElements = XMLHelperMethods.getElementNodes(element.getElementsByTagName("Condition"));

        for (Element conditionElement : conditionElements) {
            List<Element> deviceElements = XMLHelperMethods.getElementNodes(conditionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    LOGGER.log(Level.SEVERE, "Device not found: " + deviceName);
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> parameterElements = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes());

                for (Element parameterElement : parameterElements) {
                    String parameterName = parameterElement.getNodeName();
                    String operator = parameterElement.getAttribute("operator");

                    LOGGER.log(Level.INFO, "Parsing Parameter with name: " + parameterName);

                    if (operator.isEmpty()) operator = "==";

                    Parameter<?> parameter = PARAMETER_REGISTRY.getParameter(deviceInstance, parameterName);

                    if (parameter == null) {
                        LOGGER.log(Level.SEVERE, "Parameter " + parameterName + " not found");
                        throw new IllegalParameterException("Parameter " + parameterName + " not found");
                    }

                    if (parameter.getType() != null) {
                        try {
                            Value<?> expectedValue = ValueHandler.parseValue(parameterElement.getFirstChild(), parameter.getType());
                            LOGGER.log(Level.INFO, "Parameter Value has been parsed");
                            conditions.add(new Condition(parameter, operator, expectedValue));
                        } catch (InstantiationException | IllegalAccessException e) {
                            LOGGER.log(Level.SEVERE, "Failed to parse value for parameter " + parameterName, e);
                            throw new RuntimeException("Failed to parse value for parameter " + parameterName, e);
                        }
                    }

                    List<Element> subParameterElements = XMLHelperMethods.getElementNodes(parameterElement.getChildNodes());

                    for (Element subParameterElement : subParameterElements) {
                        String subParameterName = subParameterElement.getNodeName();
                        String subParameterOperator = subParameterElement.getAttribute("operator");
                        LOGGER.log(Level.INFO, "Parsing Subparameter with name: " + subParameterName);
                        Parameter<?> subParameter = parameter.getSubParameter(subParameterName);

                        if (subParameter == null) {
                            LOGGER.log(Level.SEVERE, "Subparameter " + subParameterName + " not found");
                            throw new IllegalParameterException("Subparameter " + subParameterName + " not found");
                        }

                        if (subParameter.getType() != null) {
                            try {
                                Value<?> subExpectedValue = ValueHandler.parseValue(subParameterElement.getFirstChild(), subParameter.getType());
                                LOGGER.log(Level.INFO, "Subparameter Value has been parsed");
                                conditions.add(new Condition(subParameter, subParameterOperator, subExpectedValue));
                            } catch (InstantiationException | IllegalAccessException e) {
                                LOGGER.log(Level.SEVERE, "Failed to parse value for parameter " + parameterName, e);
                                throw new RuntimeException("Failed to parse value for parameter " + parameterName, e);
                            }
                        }
                    }
                }
            }
        }

        return conditions;
    }

    private static List<Action> processActions(Element element, Map<String, DTDevice> availableDeviceDrivers) {
        List<Action> actions = new ArrayList<>();
        List<Element> actionElements = XMLHelperMethods.getElementNodes(element.getElementsByTagName("Action"));

        for (Element actionElement : actionElements) {
            List<Element> deviceElements = XMLHelperMethods.getElementNodes(actionElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                DTDevice deviceInstance = availableDeviceDrivers.get(deviceName);

                if (deviceInstance == null) {
                    LOGGER.log(Level.SEVERE, "Device not found: " + deviceName);
                    throw new IllegalParameterException("Device not found: " + deviceName);
                }

                List<Element> parameterElements = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes());

                for (Element parameterElement : parameterElements) {
                    String parameterName = parameterElement.getNodeName();

                    LOGGER.log(Level.INFO, "Parsing Parameter with name: " + parameterName);

                    Parameter<?> parameter = PARAMETER_REGISTRY.getParameter(deviceInstance, parameterName);

                    if (parameter == null) {
                        LOGGER.log(Level.SEVERE, "Parameter " + parameterName + " not found");
                        throw new IllegalParameterException("Parameter " + parameterName + " not found");
                    }

                    if (parameter.getType() != null) {
                        try {
                            Value<?> setValue = ValueHandler.parseValue(parameterElement.getFirstChild(), parameter.getType());
                            LOGGER.log(Level.INFO, "Parameter Value has been parsed");
                            actions.add(new Action(parameter, setValue));
                        } catch (InstantiationException | IllegalAccessException e) {
                            LOGGER.log(Level.SEVERE, "Failed to parse value for parameter " + parameterName, e);
                            throw new RuntimeException("Failed to parse value for parameter " + parameterName, e);
                        }
                    }

                    List<Element> subParameterElements = XMLHelperMethods.getElementNodes(parameterElement.getChildNodes());

                    for (Element subParameterElement : subParameterElements) {
                        String subParameterName = subParameterElement.getNodeName();
                        LOGGER.log(Level.INFO, "Parsing Subparameter with name: " + subParameterName);
                        Parameter<?> subParameter = parameter.getSubParameter(subParameterName);

                        if (subParameter == null) {
                            LOGGER.log(Level.SEVERE, "Subparameter " + subParameterName + " not found");
                            throw new IllegalParameterException("Subparameter " + subParameterName + " not found");
                        }

                        if (subParameter.getType() != null) {
                            try {
                                Value<?> subSetValue = ValueHandler.parseValue(subParameterElement.getFirstChild(), subParameter.getType());
                                LOGGER.log(Level.INFO, "Subparameter Value has been parsed");
                                actions.add(new Action(subParameter, subSetValue));
                            } catch (InstantiationException | IllegalAccessException e) {
                                LOGGER.log(Level.SEVERE, "Failed to parse value for parameter " + subParameterName, e);
                                throw new RuntimeException("Failed to parse value for parameter " + subParameterName, e);
                            }
                        }
                    }
                }
            }
        }

        return actions;
    }
}