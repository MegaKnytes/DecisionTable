package com.megaknytes.ftc.decisiontable.drivers.common;

import com.megaknytes.ftc.decisiontable.drivers.DTPDriver;
import com.megaknytes.ftc.decisiontable.utils.ConfigurationException;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Driver for controlling a CRServo.
 */

public class CRServoDriver implements DTPDriver {
    private CRServo crServo;
    private static final Logger LOGGER = Logger.getLogger(CRServoDriver.class.getName());

    @Override
    public void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions) {
        crServo = opMode.hardwareMap.crservo.get(deviceName);

        try {
            crServo.setDirection(DcMotorSimple.Direction.valueOf((String) Objects.requireNonNull(deviceOptions.getOrDefault("DIRECTION", "FORWARD"))));
            crServo.setPower(Double.parseDouble((String) Objects.requireNonNull(deviceOptions.getOrDefault("POWER", "0.0"))));
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "An exception has occurred while initializing device " + deviceName);
            LOGGER.log(Level.SEVERE, e.toString());
            throw new ConfigurationException("An exception has occurred while initializing device " + deviceName);
        }
        LOGGER.log(Level.INFO, "CRServoDriver setup complete for device: " + deviceName + " with options: \n" + deviceOptions);
    }

    @Override
    public void set(String param, Object value) {
        if (param.toUpperCase().equals("POWER")) {
            crServo.setPower(Double.parseDouble((String) value));
        }
    }

    @Override
    public Object get(String value) {
        switch (value.toUpperCase()) {
            case "POWER":
                return crServo.getPower();
            default:
                return null;
        }
    }
}
