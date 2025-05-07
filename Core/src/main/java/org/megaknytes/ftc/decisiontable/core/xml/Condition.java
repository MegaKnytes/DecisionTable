package org.megaknytes.ftc.decisiontable.core.xml;

import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;

public class Condition {
    private final Parameter<?> parameter;
    private final String operator;
    private final Value<?> expectedValue;

    public Condition(Parameter<?> parameter, String operator, Value<?> expectedValue) {
        this.parameter = parameter;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    public boolean evaluate() {
        Object actualValue = parameter.getValue();
        Object expectedVal = expectedValue.getValue();

        if (actualValue == null || expectedVal == null) {
            return operator.equals("==") ? actualValue == expectedVal : actualValue != expectedVal;
        }

        switch (operator) {
            case "==":
                return actualValue.equals(expectedVal);
            case "!=":
                return !actualValue.equals(expectedVal);
            case ">":
                if (actualValue instanceof Comparable && expectedVal instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expectedVal) > 0;
                }
                break;
            case "<":
                if (actualValue instanceof Comparable && expectedVal instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expectedVal) < 0;
                }
                break;
            case ">=":
                if (actualValue instanceof Comparable && expectedVal instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expectedVal) >= 0;
                }
                break;
            case "<=":
                if (actualValue instanceof Comparable && expectedVal instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expectedVal) <= 0;
                }
                break;
        }
        return false;
    }
}