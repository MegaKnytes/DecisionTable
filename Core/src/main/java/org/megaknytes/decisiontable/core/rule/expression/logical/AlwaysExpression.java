package org.megaknytes.decisiontable.core.rule.expression.logical;

import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;

public final class AlwaysExpression implements ConditionExpression {
    @Override
    public boolean evaluate() {
        return true;
    }
}
