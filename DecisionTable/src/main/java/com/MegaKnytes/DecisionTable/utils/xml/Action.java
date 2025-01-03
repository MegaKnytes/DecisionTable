package com.MegaKnytes.DecisionTable.utils.xml;

public class Action {
    private final String device;
    private final String property;
    private final String value;

    public Action(String device, String property, String value) {
        this.device = device;
        this.property = property;
        this.value = value;
    }

    public String getDevice() {
        return device;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }
}