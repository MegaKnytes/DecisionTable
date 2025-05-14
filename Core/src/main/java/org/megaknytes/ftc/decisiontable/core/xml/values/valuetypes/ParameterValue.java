package org.megaknytes.ftc.decisiontable.core.xml.values.valuetypes;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLUtils;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

public class ParameterValue<T> implements Value<T> {
    private String deviceName;
    private String paramName;
    private Parameter<T> sourceParameter;
    private static Map<String, DTDevice> deviceInstances;
    ParameterRegistry registry = ParameterRegistry.getInstance();

    public ParameterValue() {}

    public static void setDeviceInstances(Map<String, DTDevice> instances) {
        deviceInstances = instances;
    }

    @Override
    public T parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        Element paramWrapper = (Element) parameterNode;

        Element element = XMLUtils.getFirstChildElementByName(paramWrapper, "Parameter");

        if (element == null) {
            throw new ConfigurationException("Parameter reference missing Parameter element");
        }

        Element deviceElement = XMLUtils.getFirstChildElementByName(element, "Device");

        if (deviceElement == null) {
            throw new ConfigurationException("Parameter reference missing Device element");
        }

        Element deviceTypeElement = null;

        for (int i = 0; i < deviceElement.getChildNodes().getLength(); i++) {
            Node child = deviceElement.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                deviceTypeElement = (Element) child;
                deviceName = deviceTypeElement.getNodeName();
            }
        }

        if (deviceTypeElement == null) {
            throw new ConfigurationException("Device element has no device type child");
        }

        Element paramElement = XMLUtils.getFirstChildElementByName(element, "Parameter");

        if (paramElement == null) {
            throw new ConfigurationException("Parameter reference missing Parameter element");
        }

        paramName = paramElement.getTextContent().trim();

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (deviceName == null || paramName == null) {
            throw new ConfigurationException("Device name or parameter name not set");
        }

        if (sourceParameter == null) {
            try {
                DTDevice device = deviceInstances.get(deviceName);

                try {
                    Parameter<?> param = registry.getParameter(device, paramName);
                    if (param == null) {
                        throw new ConfigurationException("Parameter " + paramName + " not found in device " + deviceName);
                    }
                    this.sourceParameter = (Parameter<T>) param;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize source parameter: " + e.getMessage());
                }
            } catch (Exception e) {
                return null;
            }
        }

        try {
            return sourceParameter.getValue();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get parameter value: " + e.getMessage());
        }
    }

    @Override
    public Class<?> getType() {
        if (sourceParameter != null) {
            return sourceParameter.getType();
        }
        return Object.class;
    }
}