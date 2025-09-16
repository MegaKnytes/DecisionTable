package org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset;

import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean evaluate() {
        Object actualValue = parameter.getValue();
        Object expected = expectedValue.getValue();

        switch (operator) {
            case "==":
                return actualValue.equals(expected);
            case "!=":
                return !actualValue.equals(expected);
            case ">":
                if (actualValue instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expected) > 0;
                }
                throw new IllegalArgumentException("Cannot use '>' with non-comparable type: " + actualValue.getClass());
            case "<":
                if (actualValue instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expected) < 0;
                }
                throw new IllegalArgumentException("Cannot use '<' with non-comparable type: " + actualValue.getClass());
            case ">=":
                if (actualValue instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expected) >= 0;
                }
                throw new IllegalArgumentException("Cannot use '>=' with non-comparable type: " + actualValue.getClass());
            case "<=":
                if (actualValue instanceof Comparable) {
                    return ((Comparable) actualValue).compareTo(expected) <= 0;
                }
                throw new IllegalArgumentException("Cannot use '<=' with non-comparable type: " + actualValue.getClass());
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}