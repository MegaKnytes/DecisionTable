package org.megaknytes.decisiontable.core.rule.value.snapshot;

import org.megaknytes.decisiontable.core.rule.address.Address;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Snapshot {
    private final Map<Address, SnapshotValue> values = new HashMap<>();
    private long refreshCounter = 0;

    public SnapshotValue valueOf(Address address, Supplier<Object> reader) {
        SnapshotValue existing = values.get(address);
        if (existing != null) {
            return existing;
        }

        SnapshotValue cell = new SnapshotValue(address, reader);
        values.put(address, cell);
        return cell;
    }

    public void refreshAll() {
        refreshCounter++;

        for (SnapshotValue cell : values.values()) {
            cell.refresh();
        }
    }

    public int size() {
        return values.size();
    }

    public long getRefreshCounter() {
        return refreshCounter;
    }
}