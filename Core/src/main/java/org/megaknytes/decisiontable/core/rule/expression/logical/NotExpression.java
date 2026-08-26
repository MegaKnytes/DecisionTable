package org.megaknytes.decisiontable.core.rule.expression.logical;

import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;

public final class NotExpression implements ConditionExpression {
    private final ConditionExpression child;

    public NotExpression(ConditionExpression child) {
        this.child = child;
    }

    @Override
    public boolean evaluate() {
        return !child.evaluate();
    }
}