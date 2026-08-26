package org.megaknytes.decisiontable.core.rule.value.type;

public final class State {
    private final String typeName;
    private final String value;

    public State(String typeName, String value) {
        this.typeName = typeName;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State that = (State) o;
        return typeName.equals(that.typeName) && value.equals(that.value);
    }

    @Override
    public String toString() {
        return value;
    }
}