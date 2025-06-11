package org.megaknytes.ftc.decisiontable.core.xml.structure.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ParameterGroup {
    private final String name;
    private final Map<String, Parameter<?>> parameters = new HashMap<>();

    public ParameterGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public <T> Parameter<T> addParameter(String parameterName, Class<T> type, Supplier<T> defaultValueSupplier) {
        Parameter<T> param = new Parameter<>(parameterName, type, defaultValueSupplier);
        parameters.put(parameterName, param);
        return param;
    }

    public <T> void addParameter(String parameterName, Class<T> type, Supplier<T> defaultValueSupplier, Consumer<T> listener) {
        Parameter<T> param = addParameter(parameterName, type, defaultValueSupplier);
        param.addListener(listener);
    }

    public Parameter<?> getParameter(String parameterName) {
        return parameters.get(parameterName);
    }

    public Map<String, Parameter<?>> getAllParameters() {
        return parameters;
    }
}
