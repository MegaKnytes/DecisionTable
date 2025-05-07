package org.megaknytes.ftc.decisiontable.core.xml.values.types;

import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;

import java.util.logging.Logger;

public class ParameterValue extends Value<Object> {
    private static final Logger LOGGER = Logger.getLogger(ParameterValue.class.getName());
    private final String deviceName;
    private final String parameterName;
    private Parameter<?> resolvedParameter;

    public ParameterValue(String deviceName, String parameterName) {
        super(null);
        this.deviceName = deviceName;
        this.parameterName = parameterName;
    }

    private void resolveParameter() {
        if (resolvedParameter == null) {
            ParameterRegistry registry = ParameterRegistry.getInstance();
            String fullName = deviceName + "." + parameterName;
            resolvedParameter = registry.getParameter(fullName);
        }
    }

    @Override
    public Object getValue() {
        resolveParameter();
        return resolvedParameter.getValue();
    }
}