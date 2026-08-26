package org.megaknytes.decisiontable.core.rule.value.type.registry;

import org.megaknytes.decisiontable.core.rule.value.type.Parameter;
import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.utils.exception.IllegalParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A registry of the parameters each device has
 */
public class ParameterRegistry {

    private final Map<Device, Map<String, Parameter<?>>> deviceParameters = new HashMap<>();

    public ParameterRegistry() {
    }

    public Parameter<?> getParameter(Device device, String parameterName) {
        Map<String, Parameter<?>> deviceParameterSet = deviceParameters.get(device);

        if (deviceParameterSet == null) {
            throw new IllegalParameterException("No parameters were found for the specified device, please ensure that your driver was discovered");
        }

        Parameter<?> parameter = deviceParameterSet.get(parameterName);

        if (parameter == null) {
            throw new IllegalParameterException("Parameter, '" + parameterName + "' not found in device " + device.getDeviceName() + ", Check the device driver documentation for the correct parameter name");
        }

        return parameter;
    }

    public <T> ParameterBuilder createParameter(Device device, String parameterName, Class<T> type, Supplier<T> getter) {
        Map<String, Parameter<?>> parameters = deviceParameters.computeIfAbsent(device, k -> new HashMap<>());
        requireParameterToBeUnregistered(device, parameterName, parameters);

        Parameter<T> parameter = new Parameter<>(type, getter);
        parameters.put(parameterName, parameter);

        return new ParameterBuilder(parameter);
    }

    public <T> ParameterBuilder createParameter(Device device, String parameterName, Class<T> type, Supplier<T> getter, Consumer<T> listener) {
        Map<String, Parameter<?>> parameters = deviceParameters.computeIfAbsent(device, k -> new HashMap<>());
        requireParameterToBeUnregistered(device, parameterName, parameters);

        Parameter<T> parameter = new Parameter<>(type, getter, listener);
        parameters.put(parameterName, parameter);

        return new ParameterBuilder(parameter);
    }

    /**
     * Registers a group parameter for device tp hold subparameters (has no functionality)
     */
    public <T> ParameterBuilder createParameterGroup(Device device, String parameterName) {
        Map<String, Parameter<?>> parameters = deviceParameters.computeIfAbsent(device, k -> new HashMap<>());
        requireParameterToBeUnregistered(device, parameterName, parameters);

        Parameter<T> parameter = new Parameter<>();
        parameters.put(parameterName, parameter);

        return new ParameterBuilder(parameter);
    }

    private static void requireParameterToBeUnregistered(Device device, String parameterName, Map<String, Parameter<?>> parameters) {
        if (parameters.containsKey(parameterName)) {
            throw new IllegalParameterException("Parameter '" + parameterName + "' is already registered for device '" + device.getDeviceName() + "'");
        }
    }

    public static class ParameterBuilder {
        private final Parameter<?> parameter;

        public ParameterBuilder(Parameter<?> parameter) {
            this.parameter = parameter;
        }

        public <T> ParameterBuilder addSubParameter(String parameterName, Class<T> type, Supplier<T> getter, Consumer<T> listener) {
            parameter.addSubParameter(parameterName, new Parameter<>(type, getter, listener));
            return this;
        }

        public <T> ParameterBuilder addSubParameter(String parameterName, Class<T> type, Supplier<T> getter) {
            parameter.addSubParameter(parameterName, new Parameter<>(type, getter));
            return this;
        }
    }
}