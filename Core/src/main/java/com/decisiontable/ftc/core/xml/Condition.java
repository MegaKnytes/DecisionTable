package com.decisiontable.ftc.core.xml;

import com.decisiontable.ftc.core.xml.parameters.Parameter;
import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents a condition in a decision table.
 * <p>
 * A condition consists of a parameter, an operator, and an expected value.
 * The condition is evaluated by comparing the actual value of the parameter
 * with the expected value using the specified operator.
 */
public class Condition {
    /**
     *
     */
    private final Parameter<?> parameter;   // The parameter to be evaluated

    /**
     * The operator to be used for comparison
     */
    private final String operator;

    /**
     * The expected value to compare against
     */
    private final Value<?> expectedValue;

    /**
     * Constructs a new condition instance.
     *
     * @param parameter     The parameter to be evaluated
     * @param operator      The operator to be used for comparison
     * @param expectedValue The expected value to compare against
     */
    public Condition(Parameter<?> parameter, String operator, Value<?> expectedValue) {
        this.parameter = parameter;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    /**
     * Evaluates the condition by comparing the actual value of the parameter
     * with the expected value using the specified operator.
     * <p>
     *
     * @return `true` if the condition is satisfied based on the operator and values; `false` otherwise.
     */
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