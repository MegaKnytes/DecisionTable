package org.megaknytes.ftc.decisiontable.core.xml.registry;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ParameterRegistry {

    private static final ParameterRegistry INSTANCE = new ParameterRegistry();

    private final Map<DTDevice, Map<String, Parameter<?>>> deviceParameters = new HashMap<>();

    private ParameterRegistry() {
    }

    public static void reset() {
        INSTANCE.deviceParameters.clear();
    }

    public static ParameterRegistry getInstance() {
        return INSTANCE;
    }

    public Parameter<?> getParameter(DTDevice device, String parameterName) {
        Map<String, Parameter<?>> deviceParameterSet = deviceParameters.get(device);
        if (deviceParameterSet == null) {
            throw new IllegalParameterException("No parameters found for the specified device.");
        }
        Parameter<?> parameter = deviceParameterSet.get(parameterName);
        if (parameter == null) {
            throw new IllegalParameterException("Parameter not found: " + parameterName);
        }
        return parameter;
    }

    public <T> ParameterBuilder createParameter(DTDevice device, String parameterName, Class<T> type, Supplier<T> getter) {
        Map<String, Parameter<?>> parameters = deviceParameters.computeIfAbsent(device, k -> new HashMap<>());
        if (parameters.containsKey(parameterName)) {
            return new ParameterBuilder(parameters.get(parameterName));
        }
        Parameter<T> parameter = new Parameter<>(type, getter);
        parameters.put(parameterName, parameter);
        return new ParameterBuilder(parameter);
    }

    public <T> ParameterBuilder createParameter(DTDevice device, String parameterName, Class<T> type, Supplier<T> getter, Consumer<T> listener) {
        Map<String, Parameter<?>> parameters = deviceParameters.computeIfAbsent(device, k -> new HashMap<>());
        if (parameters.containsKey(parameterName)) {
            return new ParameterBuilder(parameters.get(parameterName));
        }
        Parameter<T> parameter = new Parameter<>(type, getter, listener);
        parameters.put(parameterName, parameter);
        return new ParameterBuilder(parameter);
    }

    public <T> ParameterBuilder createParameter(DTDevice device, String parameterName) {
        Map<String, Parameter<?>> parameters = deviceParameters.computeIfAbsent(device, k -> new HashMap<>());
        if (parameters.containsKey(parameterName)) {
            return new ParameterBuilder(parameters.get(parameterName));
        }
        Parameter<T> parameter = new Parameter<>();
        parameters.put(parameterName, parameter);
        return new ParameterBuilder(parameter);
    }

    public static class ParameterBuilder {
        private final Parameter<?> parameter;

        public ParameterBuilder(Parameter<?> parameter) {
            this.parameter = parameter;
        }

        public <T> ParameterBuilder addDependentParameter(String parameterName, Class<T> type, Supplier<T> getter, Consumer<T> listener) {
            parameter.addSubParameter(parameterName, new Parameter<>(type, getter, listener));
            return this;
        }

        public <T> ParameterBuilder addDependentParameter(String parameterName, Class<T> type, Supplier<T> getter) {
            parameter.addSubParameter(parameterName, new Parameter<>(type, getter));
            return this;
        }
    }
}