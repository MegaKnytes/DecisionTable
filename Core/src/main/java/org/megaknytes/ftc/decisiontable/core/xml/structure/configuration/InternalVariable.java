package org.megaknytes.ftc.decisiontable.core.xml.structure.configuration;

public class InternalVariable<T> {
    private final String name;
    private final String description;
    private final T value;
    private final Class<T> type;

    public InternalVariable(String name, String description, T value, Class<T> type) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.type = type;
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

    public Class<T> getType() {
        return type;
    }
}