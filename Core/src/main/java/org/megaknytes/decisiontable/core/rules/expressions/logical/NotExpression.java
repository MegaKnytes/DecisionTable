package org.megaknytes.decisiontable.core.rules.expressions.logical;

import org.megaknytes.decisiontable.core.rules.expressions.ConditionExpression;

public final class NotExpression implements ConditionExpression {
    private final ConditionExpression child;

    public NotExpression(ConditionExpression child) {
        this.child = child;
    }

    public ConditionExpression getChild() {
        return child;
    }

    @Override
    public boolean evaluate() {
        return !child.evaluate();
    }
}