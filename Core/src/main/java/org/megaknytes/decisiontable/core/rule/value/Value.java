package org.megaknytes.decisiontable.core.rule.value;

import org.megaknytes.decisiontable.core.rule.address.Address;

import java.util.Objects;

public final class Value<T> {
    private final T literal;
    private final Address referenceAddress;

    private Value(T literal, Address reference) {
        this.literal = literal;
        this.referenceAddress = reference;
    }

    public static <T> Value<T> literal(T value) {
        return new Value<>(value, null);
    }

    public static <T> Value<T> reference(Address address) {
        Objects.requireNonNull(address, "address");
        return new Value<>(null, address);
    }

    public boolean getReferenceAddress() {
        return referenceAddress != null;
    }

    public T getLiteral() {
        if (getReferenceAddress()) {
            throw new IllegalStateException("ValueSource '" + this + "' is a reference, not a literal");
        }

        return literal;
    }

    public Address getReference() {
        if (!getReferenceAddress()) {
            throw new IllegalStateException("ValueSource '" + this + "' is a literal, not a reference");
        }

        return referenceAddress;
    }

    @Override
    public String toString() {
        return getReferenceAddress() ? referenceAddress.toString() : String.valueOf(literal);
    }
}