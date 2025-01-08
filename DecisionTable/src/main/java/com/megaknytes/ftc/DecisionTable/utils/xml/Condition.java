package com.megaknytes.ftc.DecisionTable.utils.xml;

/**
 * Represents a condition in a rule, specifying a device, property, comparison, and value
 */
public class Condition {
    private final String device;
    private final String property;
    private final String comparison;
    private final String value;

    /**
     * Constructs a Condition object
     *
     * @param device     The name of the device used in the condition
     * @param property   The property of the device to be checked
     * @param comparison The type of comparison to be made
     * @param value      The value to compare the property against
     */
    public Condition(String device, String property, String comparison, String value) {
        this.device = device;
        this.property = property;
        this.comparison = comparison;
        this.value = value;
    }

    /**
     * Gets the device involved in the condition
     *
     * @return The device.
     */
    public String getDevice() {
        return device;
    }

    /**
     * Gets the property of the device to be checked
     *
     * @return The property.
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets the type of comparison to be made
     *
     * @return The comparison type
     */
    public String getComparison() {
        return comparison;
    }

    /**
     * Gets the value to compare the property against
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }
}