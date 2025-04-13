package com.megaknytes.ftc.decisiontable.drivers.common;

import com.megaknytes.ftc.decisiontable.drivers.DTPDriver;
import com.megaknytes.ftc.decisiontable.utils.ConfigurationException;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Driver for controlling a CRServo.
 */
public class DCMotorDriver implements DTPDriver {
    private DcMotor dcMotor;
    private static final Logger LOGGER = Logger.getLogger(DCMotorDriver.class.getName());

    @Override
    public void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions) {
        dcMotor = opMode.hardwareMap.dcMotor.get(deviceName);
        try {
            dcMotor.setDirection(DcMotorSimple.Direction.valueOf((String) Objects.requireNonNull(deviceOptions.getOrDefault("DIRECTION", "FORWARD"))));
            dcMotor.setTargetPosition((Integer.parseInt((String) Objects.requireNonNull(deviceOptions.getOrDefault("TARGET_POSITION", "0")))));
            dcMotor.setMode(DcMotor.RunMode.valueOf((String) Objects.requireNonNull(deviceOptions.getOrDefault("MODE", "RUN_USING_ENCODER"))));
            dcMotor.setPower(Double.parseDouble((String) Objects.requireNonNull(deviceOptions.getOrDefault("POWER", "0.0"))));
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "An exception has occurred while initializing device " + deviceName);
            LOGGER.log(Level.SEVERE, e.toString());
            throw new ConfigurationException("An exception has occurred while initializing device " + deviceName);
        }
        LOGGER.log(Level.INFO, "DCMotorDriver setup complete for device: " + deviceName + " with options: \n" + deviceOptions);
    }

    @Override
    public void set(String param, Object value) {
        if (Objects.equals(param, "POWER")) {
            dcMotor.setPower(Double.parseDouble((String) value));
            System.out.println("Set: " + dcMotor.getPower());
        }
    }

    @Override
    public Object get(String value) {
        switch (value.toUpperCase()) {
            case "POWER":
                System.out.println("Get: " + dcMotor.getPower());
                return dcMotor.getPower();
            default:
                return null;
        }
    }
}
