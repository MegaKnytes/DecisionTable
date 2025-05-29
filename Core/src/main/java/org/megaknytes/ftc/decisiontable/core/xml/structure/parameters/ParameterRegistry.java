package org.megaknytes.ftc.decisiontable.core.xml.structure.parameters;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ParameterRegistry {

    private static final ParameterRegistry INSTANCE = new ParameterRegistry();

    private final Map<DTDevice, Map<String, Parameter<?>>> parameters = new HashMap<>();

    private ParameterRegistry() {}

    public <T> Parameter<T> createParameter(DTDevice device, String parameterName, Class<T> type, Supplier<T> defaultValueSupplier) {
        Parameter<T> param = new Parameter<>(parameterName, type, defaultValueSupplier);
        parameters.computeIfAbsent(device, k -> new HashMap<>()).put(parameterName, param);
        return param;
    }

    public Parameter<?> getParameter(DTDevice device, String parameterName) {
        Map<String, Parameter<?>> deviceParameters = parameters.get(device);
        if (deviceParameters != null) {
            return deviceParameters.get(parameterName);
        } else {
            throw new IllegalParameterException("Parameters not yet registered before evaluation, register parameters before evaluating condition");
        }
    }

    public static ParameterRegistry getInstance() {
        return INSTANCE;
    }
}