package org.megaknytes.decisiontable.core.rules.value;

import java.util.Objects;

public final class DeclaredStateValue {
    private final String typeName;
    private final String value;

    public DeclaredStateValue(String typeName, String value) {
        this.typeName = typeName;
        this.value = value;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeclaredStateValue)) return false;
        DeclaredStateValue that = (DeclaredStateValue) o;
        return typeName.equals(that.typeName) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, value);
    }

    @Override
    public String toString() {
        return value;
    }
}
