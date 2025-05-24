package org.megaknytes.ftc.decisiontable.core.xml.values.valuetypes.primative;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Node;

public class StringValue implements Value<String> {
    private String value;

    public StringValue() {}

    @Override
    public String parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        String valueString = parameterNode.getTextContent();

        if (valueString == null || valueString.isEmpty()) {
            throw new ConfigurationException("Parameter node text content cannot be null or empty");
        }

        this.value = valueString;
        return this.value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}