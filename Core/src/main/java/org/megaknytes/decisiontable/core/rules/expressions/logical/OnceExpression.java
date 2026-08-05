package org.megaknytes.decisiontable.core.rules.expressions.logical;

import org.megaknytes.decisiontable.core.rules.expressions.ConditionExpression;

public final class OnceExpression implements ConditionExpression {
    private boolean fired = false;

    @Override
    public boolean evaluate() {
        if (fired) {
            return false;
        }

        fired = true;
        return true;
    }
}
