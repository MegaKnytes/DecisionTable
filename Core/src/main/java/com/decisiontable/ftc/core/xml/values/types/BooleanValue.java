package com.decisiontable.ftc.core.xml.values.types;

import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents a boolean value in the decision table.
 * <p>
 * This class extends the generic `Value` class to specifically handle
 * values of type `Boolean`. It provides a way to store and retrieve
 * boolean-based values in the decision table.
 */
public class BooleanValue extends Value<Boolean> {

    /**
     * Constructs a new `BooleanValue` instance with the specified boolean value.
     *
     * @param value The boolean value to be stored in this instance.
     */
    public BooleanValue(Boolean value) {
        super(value);
    }
}