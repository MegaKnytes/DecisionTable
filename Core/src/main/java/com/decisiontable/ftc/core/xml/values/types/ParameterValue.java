package com.decisiontable.ftc.core.xml.values.types;

import com.decisiontable.ftc.core.xml.parameters.Parameter;
import com.decisiontable.ftc.core.xml.parameters.ParameterRegistry;
import com.decisiontable.ftc.core.xml.values.Value;

import java.util.logging.Logger;

/**
 * Represents a parameter value in the decision table.
 * <p>
 * This class extends the generic `Value` class to handle parameter-based values.
 * It resolves the parameter dynamically using a `ParameterRegistry` and retrieves
 * its value when needed.
 */
public class ParameterValue extends Value<Object> {
    private static final Logger LOGGER = Logger.getLogger(ParameterValue.class.getName());

    /**
     * The name of the device associated with the parameter.
     */
    private final String deviceName;

    /**
     * The name of the parameter to be resolved.
     */
    private final String parameterName;

    /**
     * The resolved parameter instance, cached after resolution.
     */
    private Parameter<?> resolvedParameter;

    /**
     * Constructs a new `ParameterValue` instance with the specified device and parameter names.
     *
     * @param deviceName    The name of the device associated with the parameter.
     * @param parameterName The name of the parameter to be resolved.
     */
    public ParameterValue(String deviceName, String parameterName) {
        super(null);
        this.deviceName = deviceName;
        this.parameterName = parameterName;
    }

    /**
     * Resolves the parameter using the `ParameterRegistry` if it has not been resolved yet.
     * <p>
     * The parameter is identified by combining the device name and parameter name.
     */
    private void resolveParameter() {
        if (resolvedParameter == null) {
            ParameterRegistry registry = ParameterRegistry.getInstance();
            String fullName = deviceName + "." + parameterName;
            resolvedParameter = registry.getParameter(fullName);
        }
    }

    /**
     * Retrieves the value of the resolved parameter.
     * <p>
     * If the parameter has not been resolved yet, it resolves it first.
     *
     * @return The value of the resolved parameter.
     */
    @Override
    public Object getValue() {
        resolveParameter();
        return resolvedParameter.getValue();
    }
}