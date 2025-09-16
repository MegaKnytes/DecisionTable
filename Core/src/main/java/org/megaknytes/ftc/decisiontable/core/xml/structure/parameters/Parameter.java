package org.megaknytes.ftc.decisiontable.core.xml.structure.parameters;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Parameter<T> {
    private final Class<T> type;
    private final Supplier<T> defaultValueSupplier;
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final Map<String, Parameter<?>> dependents = new HashMap<>();

    public Parameter() {
        this.type = null;
        this.defaultValueSupplier = () -> null;
    }

    public Parameter(Class<T> type, Supplier<T> defaultValueSupplier) {
        this.type = type;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public Parameter(Class<T> type, Supplier<T> defaultValueSupplier, Consumer<T> listener) {
        this.type = type;
        this.defaultValueSupplier = defaultValueSupplier;
        listeners.add(listener);
    }

    public Parameter(Class<T> type, Supplier<T> defaultValueSupplier, Map<String, Parameter<?>> subParameterSet) {
        this.type = type;
        this.defaultValueSupplier = defaultValueSupplier;
        this.dependents.putAll(subParameterSet);
    }

    public Parameter(Class<T> type, Supplier<T> defaultValueSupplier, Map<String, Parameter<?>> subParameterSet, Consumer<T> listener) {
        this.type = type;
        this.defaultValueSupplier = defaultValueSupplier;
        this.dependents.putAll(subParameterSet);
        listeners.add(listener);
    }

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void addSubParameter(String parameterName, Parameter<?> parameter) {
        dependents.put(parameterName, parameter);
    }

    public Boolean hasSubParameter() {
        return dependents.isEmpty();
    }

    public Parameter<?> getSubParameter(String parameterName) {
        Parameter<?> parameter = dependents.get(parameterName);
        if (parameter == null) {
            throw new IllegalParameterException("Parameter with Name " + parameterName + " not found");
        }
        return parameter;
    }

    public T getValue() {
        return defaultValueSupplier.get();
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        for (Consumer<T> listener : listeners) {
            listener.accept((T) value);
        }
    }

    public Class<T> getType() {
        return type;
    }
}