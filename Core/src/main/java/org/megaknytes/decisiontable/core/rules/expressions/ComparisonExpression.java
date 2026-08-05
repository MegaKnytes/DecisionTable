package org.megaknytes.decisiontable.core.rules.expressions;

import java.util.Objects;
import java.util.function.Supplier;

public final class ComparisonExpression implements ConditionExpression {

    private final Supplier<Object> actual;
    private final Operator operator;
    private final Supplier<Object> expected;

    public ComparisonExpression(Supplier<Object> actual, Operator operator, Supplier<Object> expected) {
        this.actual = actual;
        this.operator = operator;
        this.expected = expected;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean evaluate() {
        Object actualValue = actual.get();
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
                throw new IllegalStateException("Unknown operator: " + operator);
        }
    }

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