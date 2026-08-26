package org.megaknytes.decisiontable.core.rule.expression.logical;

import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;

import java.util.Collections;
import java.util.List;

public final class OrExpression implements ConditionExpression {
    private final List<ConditionExpression> children;

    public OrExpression(List<ConditionExpression> children) {
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public boolean evaluate() {
        for (ConditionExpression child : children) {
            if (child.evaluate()) {
                return true;
            }
        }
        return false;
    }
}