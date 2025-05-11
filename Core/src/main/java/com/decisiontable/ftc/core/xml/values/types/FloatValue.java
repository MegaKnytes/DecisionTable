package com.decisiontable.ftc.core.xml.values.types;

import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents a float value in the decision table.
 * <p>
 * This class extends the generic `Value` class to specifically handle
 * values of type `Float`. It provides a way to store and retrieve
 * float-based values in the decision table.
 */
public class FloatValue extends Value<Float> {

    /**
     * Constructs a new `FloatValue` instance with the specified float value.
     *
     * @param value The float value to be stored in this instance.
     */
    public FloatValue(Float value) {
        super(value);
    }
}