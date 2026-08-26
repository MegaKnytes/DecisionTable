package org.megaknytes.decisiontable.core.rule.value.type;

public class InternalVariable<T> {
    private final String name;
    private final String description;
    private final Class<T> type;
    private final String stateTypeName;
    private T value;

    public InternalVariable(String name, String description, T value, Class<T> type, String stateTypeName) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.type = type;
        this.stateTypeName = stateTypeName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public String getStateTypeName() {
        return stateTypeName;
    }
}
