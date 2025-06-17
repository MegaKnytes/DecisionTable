package org.megaknytes.ftc.decisiontable.core.xml;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceExtended;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterGroup;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueParser;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.ParameterValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemConfigurationProcessor {
    private static final Logger LOGGER = Logger.getLogger(SystemConfigurationProcessor.class.getName());
    private static final ParameterRegistry PARAMETER_REGISTRY = ParameterRegistry.getInstance();

    public static Map<String, DTDevice> processDevices(NodeList elementNodes, OpMode opMode, Map<String, DTDevice> availableDeviceDrivers) {
        LOGGER.log(Level.INFO, "Processing devices...");

        Map<String, DTDevice> deviceInstances = new HashMap<>();
        List<Element> driverElements = XMLHelperMethods.getElementNodes(elementNodes);

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

            List<Element> deviceElements = XMLHelperMethods.getElementNodes(driverElement.getChildNodes());

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

                        ((DTDeviceExtended) deviceInstance).registerConfiguration(opMode, PARAMETER_REGISTRY);
                    } else if (DTDevice.class.isAssignableFrom(driverClass)) {
                        deviceInstance = (DTDevice) driverClass.newInstance();
                        deviceInstances.put(deviceName, deviceInstance);

                        deviceInstance.registerConfiguration(opMode.hardwareMap, PARAMETER_REGISTRY);
                    } else {
                        LOGGER.log(Level.SEVERE, "Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                        throw new ConfigurationException("Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                    }

                    List<Element> groupElements = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes());

                    for (Element groupElement : groupElements) {
                        String groupName = groupElement.getNodeName();
                        ParameterGroup group = PARAMETER_REGISTRY.getGroup(deviceInstance, groupName);

                        if (group == null) {
                            LOGGER.log(Level.SEVERE, "Group " + groupName + " not found in device " + deviceName);
                            throw new ConfigurationException("Group " + groupName + " not found in device " + deviceName);
                        }

                        List<Element> paramElements = XMLHelperMethods.getElementNodes(groupElement.getChildNodes());

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

    @SuppressWarnings("unchecked")
    public static <valueType> void processInternalVariables(NodeList internalVariablesNodes, Map<Class<?>, Value<?>> valueParserClasses) {
        InternalVariableRegistry internalVariableRegistry = InternalVariableRegistry.getInstance();
        List<Element> groupElements = XMLHelperMethods.getElementNodes(internalVariablesNodes);

        Map<String, Class<?>> typeNameMap = new HashMap<>();
        for (Class<?> type : valueParserClasses.keySet()) {
            typeNameMap.put(type.getSimpleName(), type);
        }

        for (Element groupElement : groupElements) {
            String groupName = groupElement.getNodeName();
            LOGGER.log(Level.INFO, "Processing internal variable group: " + groupName);

            List<Element> typeElements = XMLHelperMethods.getElementNodes(groupElement.getChildNodes());

            for (Element typeElement : typeElements) {
                String typeName = typeElement.getNodeName();
                Class<?> valueType = typeNameMap.get(typeName);

                if (valueType == null) {
                    LOGGER.log(Level.WARNING, "Unknown internal variable type: " + typeName);
                    continue;
                }

                List<Element> variableElements = XMLHelperMethods.getElementNodes(typeElement.getChildNodes());

                for (Element variableElement : variableElements) {
                    String variableName = variableElement.getNodeName();
                    String description = variableElement.getAttribute("description");

                    try {
                        Value<?> parsedValue = ValueParser.parseValue(variableElement, valueType);
                        Object value = parsedValue.getValue();

                        internalVariableRegistry.addVariable(groupName, variableName, description, (valueType) value, (Class<valueType>) valueType);
                        LOGGER.log(Level.INFO, "Added internal variable: " + variableName + " of type " + valueType.getSimpleName());
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error parsing internal variable " + variableName, e);
                        throw new ConfigurationException("Invalid value for " + variableName + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}