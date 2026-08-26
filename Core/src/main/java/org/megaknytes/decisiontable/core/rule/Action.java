package org.megaknytes.decisiontable.core.rule;

import org.megaknytes.decisiontable.core.rule.address.Address;
import org.megaknytes.decisiontable.core.rule.value.snapshot.SnapshotValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Action {
    private final Address address;
    private final Consumer<Object> target;
    private final Supplier<Object> value;

    public Action(Address address, Consumer<Object> target, Supplier<Object> value) {
        this.address = address;
        this.target = target;
        this.value = value;
    }

    public Address getAddress() {
        return address;
    }

    public void execute() {
        Object resolved = value instanceof SnapshotValue ? ((SnapshotValue) value).readLive() : value.get();

        if (resolved != null) {
            target.accept(resolved);
        }
    }
}