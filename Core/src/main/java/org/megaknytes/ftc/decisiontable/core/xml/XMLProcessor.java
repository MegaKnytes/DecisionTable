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

                    List<Element> initialParameters = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                    for (Element initialParameter : initialParameters) {
                        String parameterName = initialParameter.getNodeName();
                        try {
                            Parameter<?> parameter = parameterRegistry.getParameter(deviceInstance, parameterName);

                            if (parameter == null) {
                                throw new ConfigurationException("Parameter " + parameterName + " not found in device " + deviceName);
                            }

                            Value<?> value = ValueParser.parseValue(initialParameter, parameter.getType());
                            new Action(parameter, value).execute();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to initialize parameter " + parameterName + " for device " + deviceName, e);
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

                List<Element> parameterElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                for (Element parameterElement : parameterElements) {
                    String parameterName = parameterElement.getNodeName();
                    String operator = parameterElement.getAttribute("operator");
                    if (operator.isEmpty()) operator = "==";

                    Parameter<?> parameter = parameterRegistry.getParameter(deviceInstance, parameterName);

                    if (parameter == null) {
                        throw new IllegalParameterException("Parameter " + parameterName + " not found in device " + deviceName);
                    }

                    Value<?> expectedValue = ValueParser.parseValue(parameterElement, parameter.getType());
                    conditions.add(new Condition(parameter, operator, expectedValue));
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

                List<Element> parameterElements = XMLUtils.getElementNodes(deviceElement.getChildNodes());

                for (Element parameterElement : parameterElements) {
                    String parameterName = parameterElement.getNodeName();
                    Parameter<?> parameter = parameterRegistry.getParameter(deviceInstance, parameterName);

                    if (parameter == null) {
                        throw new IllegalParameterException("Parameter " + parameterName + " not found in device " + deviceName);
                    }

                    Value<?> value = ValueParser.parseValue(parameterElement, parameter.getType());
                    actions.add(new Action(parameter, value));
                }
            }
        }

        return actions;
    }
}