package org.megaknytes.decisiontable.core.rule.expression.comparison;

import org.megaknytes.decisiontable.core.rule.value.snapshot.Snapshot;
import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;

public final class CachingComparison implements ConditionExpression {
    private final ComparisonExpression delegate;
    private final Snapshot snapshot;

    private long lastRefreshCount = -1;
    private boolean lastResult;

    public CachingComparison(ComparisonExpression expression, Snapshot snapshot) {
        this.delegate = expression;
        this.snapshot = snapshot;
    }

    @Override
    public boolean evaluate() {
        long refreshCounter = snapshot.getRefreshCounter();
        if (refreshCounter != lastRefreshCount) {
            lastResult = delegate.evaluate();
            lastRefreshCount = refreshCounter;
        }
        return lastResult;
    }

    @Override
    public String toString() {
        return "Cached(" + delegate + ")";
    }
}