package org.megaknytes.decisiontable.core.rules.expressions.logical;

import org.megaknytes.decisiontable.core.rules.expressions.ConditionExpression;

public final class AlwaysExpression implements ConditionExpression {
    @Override
    public boolean evaluate() {
        return true;
    }
}
