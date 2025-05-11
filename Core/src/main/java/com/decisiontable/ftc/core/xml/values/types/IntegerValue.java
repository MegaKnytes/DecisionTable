package com.decisiontable.ftc.core.xml.values.types;

import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents an integer value in the decision table.
 * <p>
 * This class extends the generic `Value` class to specifically handle
 * values of type `Integer`. It provides a way to store and retrieve
 * integer-based values in the decision table.
 */
public class IntegerValue extends Value<Integer> {

    /**
     * Constructs a new `IntegerValue` instance with the specified integer value.
     *
     * @param value The integer value to be stored in this instance.
     */
    public IntegerValue(Integer value) {
        super(value);
    }
}