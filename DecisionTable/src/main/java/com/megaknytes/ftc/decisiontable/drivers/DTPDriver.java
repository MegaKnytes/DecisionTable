package com.megaknytes.ftc.decisiontable.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Map;

/**
 * DTPDriver is an interface that defines the methods required for setting up and controlling a Decision Table device
 */
public interface DTPDriver {

    /**
     * Sets up the device with the given parameters
     *
     * @param opMode      The currently running OpMode
     * @param deviceName  The name of the device to be set up
     * @param deviceOptions A map containing configuration options for the device
     */
    void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions);

    /**
     * Sets a parameter of the device to a specified value
     *
     * @param param The parameter to be set
     * @param value The value to set the parameter to
     */
    void set(String param, Object value);

    /**
     * Gets the value of a specified parameter of the device
     *
     * @param value The parameter whose value is to be retrieved
     * @return The value of the specified parameter
     */
    Object get(String value);
}