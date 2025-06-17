package org.megaknytes.ftc.decisiontable.core.xml.values.types;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

public class ParameterValue<T> implements Value<T> {
    private static Map<String, DTDevice> deviceInstances;
    ParameterRegistry registry = ParameterRegistry.getInstance();
    private String deviceName;
    private String groupName;
    private String paramName;
    private Parameter<T> sourceParameter;

    public ParameterValue() {
    }

    public static void setDeviceInstances(Map<String, DTDevice> instances) {
        deviceInstances = instances;
    }

    @Override
    public T parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        Element parameterElement = XMLHelperMethods.getFirstChildElementByName((Element) parameterNode, "Parameter");

        if (parameterElement == null) {
            throw new ConfigurationException("Missing Parameter element");
        }

        Element deviceElement = XMLHelperMethods.getFirstChildElement(parameterElement.getChildNodes());

        if (deviceElement != null) {
            deviceName = deviceElement.getNodeName();
        } else {
            throw new ConfigurationException("Device element missing in Parameter");
        }

        Element groupElement = XMLHelperMethods.getFirstChildElement(deviceElement.getChildNodes());

        if (groupElement != null) {
            groupName = groupElement.getNodeName();
        } else {
            throw new ConfigurationException("Group element missing in Device");
        }

        Element paramElement = XMLHelperMethods.getFirstChildElement(groupElement.getChildNodes());

        if (paramElement != null) {
            paramName = paramElement.getNodeName();
        } else {
            throw new ConfigurationException("Parameter element missing in Group");
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (deviceName == null || groupName == null || paramName == null) {
            throw new ConfigurationException("Device name, group name, or parameter name not set");
        }

        if (sourceParameter == null) {
            DTDevice device = deviceInstances.get(deviceName);
            if (device == null) {
                throw new ConfigurationException("Device not found: " + deviceName);
            }

            Parameter<?> param = registry.getParameter(device, groupName, paramName);
            this.sourceParameter = (Parameter<T>) param;
        }

        return sourceParameter.getValue();
    }

    @Override
    public Class<?> getType() {
        if (sourceParameter != null) {
            return sourceParameter.getType();
        }
        return Object.class;
    }
}