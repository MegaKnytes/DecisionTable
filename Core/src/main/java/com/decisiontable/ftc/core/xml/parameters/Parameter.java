package com.decisiontable.ftc.core.xml.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Parameter<T> {
    private final String name;
    private final Class<T> type;
    private final Supplier<T> defaultValueSupplier;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public Parameter(String name, Class<T> type, Supplier<T> defaultValueSupplier) {
        this.name = name;
        this.type = type;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public String getName() {
        return name;
    }


    public void setValue(T value) {
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public T getValue() {
        return defaultValueSupplier.get();
    }
}