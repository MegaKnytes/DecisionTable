package com.decisiontable.ftc.core.xml.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a parameter in the decision table.
 * <p>
 * This class provides a way to define parameters with a name, type, and default value supplier.
 * It also supports adding listeners that react to value changes.
 *
 * @param <T> The type of the parameter value.
 */
public class Parameter<T> {
    /**
     * The name of the parameter.
     */
    private final String name;

    /**
     * The class type of the parameter.
     */
    private final Class<T> type;

    /**
     * A supplier for the default value of the parameter.
     */
    private final Supplier<T> defaultValueSupplier;

    /**
     * A list of listeners that are notified when the parameter value changes.
     */
    private final List<Consumer<T>> listeners = new ArrayList<>();

    /**
     * Constructs a new `Parameter` instance with the specified name, type, and default value supplier.
     *
     * @param name                 The name of the parameter.
     * @param type                 The class type of the parameter.
     * @param defaultValueSupplier A supplier for the default value of the parameter.
     */
    public Parameter(String name, Class<T> type, Supplier<T> defaultValueSupplier) {
        this.name = name;
        this.type = type;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    /**
     * Retrieves the name of the parameter.
     *
     * @return The name of the parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the parameter and notifies all registered listeners.
     *
     * @param value The new value of the parameter.
     */
    public void setValue(T value) {
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }

    /**
     * Adds a listener that will be notified when the parameter value changes.
     *
     * @param listener A consumer that processes the new value of the parameter.
     */
    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    /**
     * Retrieves the current value of the parameter using the default value supplier.
     *
     * @return The current value of the parameter.
     */
    public T getValue() {
        return defaultValueSupplier.get();
    }
}