package org.megaknytes.decisiontable.core.rule.value.snapshot;

import org.megaknytes.decisiontable.core.rule.address.Address;

import java.util.function.Supplier;

public final class SnapshotValue implements Supplier<Object> {
    private final Address address;
    private final Supplier<Object> reader;
    private Object value;

    public SnapshotValue(Address address, Supplier<Object> reader) {
        this.address = address;
        this.reader = reader;
    }

    void refresh() {
        value = reader.get();
    }

    @Override
    public Object get() {
        return value;
    }

    public Object readLive() {
        return reader.get();
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Reading(" + address + "=" + value + ")";
    }
}