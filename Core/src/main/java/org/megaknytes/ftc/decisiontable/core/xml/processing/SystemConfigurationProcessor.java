package org.megaknytes.ftc.decisiontable.core.xml.processing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceEx;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.registry.InternalVariableRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueHandler;
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
            DTDevice driverTemplate = availableDeviceDrivers.get(driverName);

            LOGGER.log(Level.INFO, "Processing driver: " + driverName);

            if (!availableDeviceDrivers.containsKey(driverName) || driverTemplate == null) {
                LOGGER.log(Level.SEVERE, "Driver with name " + driverName + " not found, has it been enabled?");
                throw new DriverNotFoundException("Driver with name " + driverName + " not found, has it been enabled?");
            }

            Class<?> driverClass = driverTemplate.getClass();
            List<Element> deviceElements = XMLHelperMethods.getElementNodes(driverElement.getChildNodes());

            for (Element deviceElement : deviceElements) {
                String deviceName = deviceElement.getNodeName();
                LOGGER.log(Level.INFO, "Processing device with name: " + deviceName);

                if (deviceInstances.containsKey(deviceName)) {
                    LOGGER.log(Level.SEVERE, "Duplicate device name found: " + deviceName);
                    throw new ConfigurationException("Duplicate device name: " + deviceName);
                }

                try {
                    DTDevice deviceInstance;
                    if (DTDeviceEx.class.isAssignableFrom(driverClass)) {
                        deviceInstance = (DTDeviceEx) driverClass.newInstance();
                        deviceInstances.put(deviceName, deviceInstance);

                        ((DTDeviceEx) deviceInstance).registerConfiguration(opMode, PARAMETER_REGISTRY);
                        LOGGER.log(Level.INFO, "Device configuration has been registered to the parameter registry");
                    } else if (DTDevice.class.isAssignableFrom(driverClass)) {
                        deviceInstance = (DTDevice) driverClass.newInstance();
                        deviceInstances.put(deviceName, deviceInstance);

                        deviceInstance.registerConfiguration(opMode.hardwareMap, PARAMETER_REGISTRY);
                        LOGGER.log(Level.INFO, "Device configuration has been registered to the parameter registry");
                    } else {
                        LOGGER.log(Level.SEVERE, "Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                        throw new ConfigurationException("Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
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
                                Value<?> value = ValueHandler.parseValue(parameterElement.getFirstChild(), parameter.getType());
                                LOGGER.log(Level.INFO, "Parameter Value has been parsed");
                                new Action(parameter, value).execute();
                            } catch (NullPointerException e) {
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
                                    Value<?> value = ValueHandler.parseValue(subParameterElement.getFirstChild(), subParameter.getType());
                                    LOGGER.log(Level.INFO, "Subparameter Value has been parsed");
                                    new Action(subParameter, value).execute();
                                } catch (NullPointerException e) {
                                    LOGGER.log(Level.SEVERE, "Failed to parse value for subparameter " + subParameterName, e);
                                    throw new RuntimeException("Failed to parse value for subparameter " + subParameterName, e);
                                }
                            }
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.log(Level.SEVERE, "Failed to instantiate device class for " + deviceName + ": " + e.getMessage(), e);
                    throw new ConfigurationException("Failed to instantiate device class for " + deviceName + ": " + e.getMessage());
                }
            }
        }

        ParameterValue.setDeviceInstances(deviceInstances);

        LOGGER.log(Level.INFO, "Device Configuration Complete");

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
                        Value<?> parsedValue = ValueHandler.parseValue(variableElement, valueType);
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