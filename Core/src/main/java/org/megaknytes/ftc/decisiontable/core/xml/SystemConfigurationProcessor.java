package org.megaknytes.ftc.decisiontable.core.xml;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceExtended;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterGroup;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Rule;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.megaknytes.ftc.decisiontable.core.xml.values.ValueParser;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.ParameterValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemConfigurationProcessor {
    private static final Logger LOGGER = Logger.getLogger(SystemConfigurationProcessor.class.getName());

    public static Map<String, DTDevice> processDevices(NodeList elementNodes, OpMode opMode, Map<String, DTDevice> availableDeviceDrivers, ParameterRegistry parameterRegistry) {
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

                        ((DTDeviceExtended) deviceInstance).registerConfiguration(opMode, parameterRegistry);
                    } else if (DTDevice.class.isAssignableFrom(driverClass)) {
                        deviceInstance = (DTDevice) driverClass.newInstance();
                        deviceInstances.put(deviceName, deviceInstance);

                        deviceInstance.registerConfiguration(opMode.hardwareMap, parameterRegistry);
                    } else {
                        LOGGER.log(Level.SEVERE, "Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                        throw new ConfigurationException("Driver class " + driverClass.getName() + " is not a valid DTDevice or DTDeviceExtended");
                    }

                    List<Element> groupElements = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes());

                    for (Element groupElement : groupElements) {
                        String groupName = groupElement.getNodeName();
                        ParameterGroup group = parameterRegistry.getGroup(deviceInstance, groupName);

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
}