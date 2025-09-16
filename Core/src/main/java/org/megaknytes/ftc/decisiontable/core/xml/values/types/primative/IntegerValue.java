package org.megaknytes.ftc.decisiontable.core.xml.values.types.primative;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Node;

public class IntegerValue implements Value<Integer> {
    private Integer value;

    public IntegerValue() {
    }

    @Override
    public Integer parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        String valueString = parameterNode.getTextContent().trim();

        if (valueString.isEmpty()) {
            throw new ConfigurationException("Parameter node text content cannot be null or empty");
        }

        this.value = Integer.parseInt(valueString);
        return this.value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}