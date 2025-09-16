package org.megaknytes.ftc.decisiontable.core.xml.values.types.primative;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Node;

public class DoubleValue implements Value<Double> {
    private Double value;

    public DoubleValue() {
    }

    @Override
    public Double parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        String valueString = parameterNode.getTextContent().trim();

        if (valueString.isEmpty()) {
            throw new ConfigurationException("Parameter node text content cannot be null or empty");
        }

        this.value = Double.parseDouble(valueString);
        return this.value;
    }

    @Override
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}