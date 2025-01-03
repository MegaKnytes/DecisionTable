package com.MegaKnytes.DecisionTable.utils.xml;

public class Condition {
    private final String device;
    private final String property;
    private final String comparison;
    private final String value;

    public Condition(String device, String property, String comparison, String value) {
        this.device = device;
        this.property = property;
        this.comparison = comparison;
        this.value = value;
    }

    public String getDevice() {
        return device;
    }

    public String getProperty() {
        return property;
    }

    public String getComparison() {
        return comparison;
    }

    public String getValue() {
        return value;
    }
}
