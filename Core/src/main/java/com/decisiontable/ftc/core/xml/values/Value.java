package com.decisiontable.ftc.core.xml.values;

/**
 * Abstract base class representing a value in the decision table.
 * <p>
 * This class serves as a generic container for a value of type `T`.
 * Subclasses can extend this class to provide specific implementations
 * for different types of values.
 *
 * @param <T> The type of the value being represented.
 */
public abstract class Value<T> {
    /**
     * The value being represented.
     */
    protected T value;

    /**
     * Constructs a new `Value` instance with the specified value.
     *
     * @param value The value to be stored in this instance.
     */
    public Value(T value) {
        this.value = value;
    }

    /**
     * Retrieves the value stored in this instance.
     *
     * @return The value of type `T`.
     */
    public T getValue() {
        return value;
    }
}