package org.megaknytes.decisiontable.core.rule.expression.comparison;

import java.util.function.Supplier;

public final class Constant implements Supplier<Object> {
    private final Object value;

    public Constant(Object value) {
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}