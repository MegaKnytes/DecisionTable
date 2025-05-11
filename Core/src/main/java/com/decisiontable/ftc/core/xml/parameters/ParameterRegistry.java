package com.decisiontable.ftc.core.xml.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A registry for managing parameters in the decision table.
 * <p>
 * This class provides a centralized way to create, register, and retrieve
 * parameters.
 */
public class ParameterRegistry {
    /**
     * The instance of the `ParameterRegistry`.
     */
    private static final ParameterRegistry INSTANCE = new ParameterRegistry();

    /**
     * A map to store parameters, keyed by their full names.
     */
    private final Map<String, Parameter<?>> parameters = new HashMap<>();


    private ParameterRegistry() { }

    /**
     * Retrieves the instance of the `ParameterRegistry`.
     *
     * @return The instance of the `ParameterRegistry`.
     */
    public static ParameterRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new parameter and registers it in the registry.
     *
     * @param driverName            The name of the driver associated with the parameter.
     * @param name                  The name of the parameter.
     * @param type                  The class type of the parameter.
     * @param defaultValueSupplier  A supplier for the default value of the parameter.
     * @param <T>                   The type of the parameter value.
     * @return The newly created and registered parameter.
     */
    public <T> Parameter<T> createParameter(String driverName, String name, Class<T> type, Supplier<T> defaultValueSupplier) {
        Parameter<T> param = new Parameter<>(name, type, defaultValueSupplier);
        return registerParameter(driverName + "." + name, param);
    }

    /**
     * Registers a parameter in the registry.
     *
     * @param fullName  The full name of the parameter (e.g., "driverName.parameterName").
     * @param parameter The parameter to register.
     * @param <T>       The type of the parameter value.
     * @return The registered parameter.
     */
    public <T> Parameter<T> registerParameter(String fullName, Parameter<T> parameter) {
        parameters.put(fullName, parameter);
        return parameter;
    }

    /**
     * Retrieves a parameter from the registry by its full name.
     *
     * @param fullName The full name of the parameter to retrieve.
     * @return The parameter associated with the given full name, or `null` if not found.
     */
    public Parameter<?> getParameter(String fullName) {
        return parameters.get(fullName);
    }
}