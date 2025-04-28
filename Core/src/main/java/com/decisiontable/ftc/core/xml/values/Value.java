package com.decisiontable.ftc.core.xml.values;

public abstract class Value<T> {
    protected T value;

    public Value(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

}