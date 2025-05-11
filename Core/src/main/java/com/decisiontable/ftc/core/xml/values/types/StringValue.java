package com.decisiontable.ftc.core.xml.values.types;

import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents a string value.
 * <p>
 * This class extends the generic `Value` class to specifically handle
 * values of type `String`. It provides a way to store and retrieve
 * string-based values in the decision table.
 */
public class StringValue extends Value<String> {

    /**
     * Constructs a new `StringValue` instance with the specified string value.
     *
     * @param value The string value to be stored in this instance.
     */
    public StringValue(String value) {
        super(value);
    }
}