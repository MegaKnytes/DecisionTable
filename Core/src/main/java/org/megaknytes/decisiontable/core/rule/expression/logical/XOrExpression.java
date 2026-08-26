package org.megaknytes.decisiontable.core.rule.expression.logical;

import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;

import java.util.Collections;
import java.util.List;

public final class XOrExpression implements ConditionExpression {
    private final List<ConditionExpression> children;

    public XOrExpression(List<ConditionExpression> children) {
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public boolean evaluate() {
        int trueCount = 0;

        for (ConditionExpression child : children) {
            if (!child.evaluate()) {
                continue;
            }

            trueCount++;
            if (trueCount > 1) {
                return false;
            }
        }

        return trueCount == 1;
    }
}