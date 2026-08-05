package org.megaknytes.decisiontable.core.rules;

import org.megaknytes.decisiontable.core.rules.address.TargetAddress;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Action {
    private final TargetAddress targetAddress;
    private final Consumer<Object> target;
    private final Supplier<Object> value;

    public Action(TargetAddress targetAddress, Consumer<Object> target, Supplier<Object> value) {
        this.targetAddress = targetAddress;
        this.target = target;
        this.value = value;
    }

    public TargetAddress getTargetAddress() {
        return targetAddress;
    }

    public void execute() {
        Object resolved = value.get();

        if (resolved != null) {
            target.accept(resolved);
        }
    }
}