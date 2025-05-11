package com.decisiontable.ftc.core.xml.values.types;

import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents a double value in the decision table.
 * <p>
 * This class extends the generic `Value` class to specifically handle
 * values of type `Double`. It provides a way to store and retrieve
 * double-based values in the decision table.
 */
public class DoubleValue extends Value<Double> {

    /**
     * Constructs a new `DoubleValue` instance with the specified double value.
     *
     * @param value The double value to be stored in this instance.
     */
    public DoubleValue(Double value) {
        super(value);
    }
}