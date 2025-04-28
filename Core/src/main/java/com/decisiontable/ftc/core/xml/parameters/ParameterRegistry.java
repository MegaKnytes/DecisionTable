package com.decisiontable.ftc.core.xml.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ParameterRegistry {
    private static final ParameterRegistry INSTANCE = new ParameterRegistry();
    private final Map<String, Parameter<?>> parameters = new HashMap<>();

    private ParameterRegistry() { }

    public static ParameterRegistry getInstance() {
        return INSTANCE;
    }

    public <T> Parameter<T> createParameter(String driverName, String name, Class<T> type, Supplier<T> defaultValueSupplier) {
        Parameter<T> param = new Parameter<>(name, type, defaultValueSupplier);
        return registerParameter(driverName + "." + name, param);
    }

    public <T> Parameter<T> registerParameter(String fullName, Parameter<T> parameter) {
        parameters.put(fullName, parameter);
        return parameter;
    }

    public Parameter<?> getParameter(String fullName) {
        return parameters.get(fullName);
    }
}