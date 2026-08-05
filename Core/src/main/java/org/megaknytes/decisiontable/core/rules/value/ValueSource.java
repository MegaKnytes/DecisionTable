package org.megaknytes.decisiontable.core.rules.value;

import org.megaknytes.decisiontable.core.rules.address.TargetAddress;

import java.util.Objects;

public final class ValueSource<T> {
    private final T literal;
    private final TargetAddress reference;

    private ValueSource(T literal, TargetAddress reference) {
        this.literal = literal;
        this.reference = reference;
    }

    public static <T> ValueSource<T> literal(T value) {
        return new ValueSource<>(value, null);
    }

    public static <T> ValueSource<T> reference(TargetAddress address) {
        Objects.requireNonNull(address, "address");
        return new ValueSource<>(null, address);
    }

    public boolean isReference() {
        return reference != null;
    }

    public T getLiteral() {
        if (isReference()) {
            throw new IllegalStateException("ValueSource '" + this + "' is a reference, not a literal");
        }

        return literal;
    }

    public TargetAddress getReference() {
        if (!isReference()) {
            throw new IllegalStateException("ValueSource '" + this + "' is a literal, not a reference");
        }

        return reference;
    }

    @Override
    public String toString() {
        return isReference() ? reference.toString() : String.valueOf(literal);
    }
}