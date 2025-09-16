package org.megaknytes.ftc.decisiontable.core.xml.values.types.primative;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Node;

public class BooleanValue implements Value<Boolean> {
    private Boolean value;

    public BooleanValue() {
    }

    @Override
    public Boolean parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        String valueString = parameterNode.getTextContent().trim();

        if (valueString.isEmpty()) {
            throw new ConfigurationException("Parameter node text content cannot be null or empty");
        }

        this.value = Boolean.parseBoolean(valueString);
        return this.value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}