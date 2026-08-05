package org.megaknytes.decisiontable.core.rules.expressions.logical;

import org.megaknytes.decisiontable.core.rules.expressions.ConditionExpression;

import java.util.Collections;
import java.util.List;

public final class AndExpression implements ConditionExpression {
    private final List<ConditionExpression> children;

    public AndExpression(List<ConditionExpression> children) {
        this.children = Collections.unmodifiableList(children);
    }

    public List<ConditionExpression> getChildren() {
        return children;
    }

    @Override
    public boolean evaluate() {
        for (ConditionExpression child : children) {
            if (!child.evaluate()) {
                return false;
            }
        }
        return true;
    }
}