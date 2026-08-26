package org.megaknytes.decisiontable.core.rule.expression.comparison;

import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A condition that compares a value against an expected value using a given operator
 */
public final class ComparisonExpression implements ConditionExpression {
    private final Supplier<Object> current;
    private final Operator operator;
    private final Supplier<Object> expected;

    public ComparisonExpression(Supplier<Object> current, Operator operator, Supplier<Object> expected) {
        this.current = current;
        this.operator = operator;
        this.expected = expected;
    }

    @Override
    public boolean evaluate() {
        Object actualValue = current.get();
        Object expectedValue = expected.get();

        switch (operator) {
            case EQUALS:
                return Objects.equals(actualValue, expectedValue);
            case NOT_EQUALS:
                return !Objects.equals(actualValue, expectedValue);
            case GREATER_THAN:
                return compare(actualValue, expectedValue) > 0;
            case LESS_THAN:
                return compare(actualValue, expectedValue) < 0;
            case GREATER_OR_EQUAL:
                return compare(actualValue, expectedValue) >= 0;
            case LESS_OR_EQUAL:
                return compare(actualValue, expectedValue) <= 0;
            default:
                throw new ConfigurationException("Unknown operator type: " + operator);
        }
    }

    // Result is an int: -1: less than, 0: equal, 1: greater than
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compare(Object actualValue, Object expectedValue) {
        if (!(actualValue instanceof Comparable)) {
            throw new IllegalArgumentException("Cannot use '" + operator + "' with non-comparable type: " + (actualValue == null ? "null" : actualValue.getClass()));
        }

        return ((Comparable) actualValue).compareTo(expectedValue);
    }

    public enum Operator {
        EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL, LESS_OR_EQUAL
    }
}