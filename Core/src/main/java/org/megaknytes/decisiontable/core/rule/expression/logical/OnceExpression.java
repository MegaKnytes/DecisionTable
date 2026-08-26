package org.megaknytes.decisiontable.core.rule.expression.logical;

import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;

public final class OnceExpression implements ConditionExpression {
    private boolean triggered = false;

    @Override
    public boolean evaluate() {
        if (triggered) {
            return false;
        }

        triggered = true;
        return true;
    }
}