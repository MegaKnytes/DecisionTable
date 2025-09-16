package org.megaknytes.ftc.decisiontable.core.xml.values;

import org.w3c.dom.Node;

/**
 * Represents a value in the decision table.
 */
public interface Value<T> {
    T parseValue(Node parameterNode);
    void setValue(T value);

    T getValue();

    Class<?> getType();
}