package com.decisiontable.ftc.core.utils.xml;

/**
 * Represents an action in a rule, specifying a device, property, and value
 */
public class Action {
    private final String device;
    private final String property;
    private final String value;

    /**
     * Constructs an Action object
     *
     * @param device   The device involved in the action
     * @param property The property of the device to be set
     * @param value    The value to set the property to
     */
    public Action(String device, String property, String value) {
        this.device = device;
        this.property = property;
        this.value = value;
    }

    /**
     * Gets the device involved in the action
     *
     * @return The device.
     */
    public String getDevice() {
        return device;
    }

    /**
     * Gets the property of the device to be set
     *
     * @return The property
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets the value to set the property to
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }
}