package org.megaknytes.ftc.decisiontable.core.xml.values.types;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParameterValue<T> implements Value<T> {
    private Parameter<T> sourceParameter;
    private static Map<String, DTDevice> deviceInstances;
    private static final ParameterRegistry PARAMETER_REGISTRY = ParameterRegistry.getInstance();
    private static final Logger LOGGER = Logger.getLogger(ParameterValue.class.getName());

    public ParameterValue() {
    }

    public static void setDeviceInstances(Map<String, DTDevice> instances) {
        deviceInstances = instances;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parseValue(Node parameterNode) {
        Element deviceElement = (Element) parameterNode;
        String deviceName = deviceElement.getNodeName();
        Element parameterElement = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes()).get(0);

        String parameterName = parameterElement.getNodeName();
        DTDevice deviceInstance = deviceInstances.get(deviceName);

        Parameter<T> parameter = (Parameter<T>) PARAMETER_REGISTRY.getParameter(deviceInstance, parameterName);

        if (parameter == null) {
            LOGGER.log(Level.SEVERE, "Parameter " + parameterName + " not found");
            throw new IllegalParameterException("Parameter " + parameterName + " not found");
        }

        if (parameter.getType() != null) {
            this.sourceParameter = parameter;
        } else if (parameterElement.getChildNodes().getLength() > 1) {
            Element subParameterElement = XMLHelperMethods.getElementNodes(deviceElement.getChildNodes()).get(0);
            this.sourceParameter = (Parameter<T>) parameter.getSubParameter(subParameterElement.getNodeName());
        }

        return null;
    }

    @Override
    public void setValue(T value) {
        if (sourceParameter == null) {
            throw new ConfigurationException("Parameter reference was not correctly initialized");
        }

        sourceParameter.setValue(value);
    }

    @Override
    public T getValue() {
        if (sourceParameter == null) {
            throw new ConfigurationException("Parameter reference was not correctly initialized");
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