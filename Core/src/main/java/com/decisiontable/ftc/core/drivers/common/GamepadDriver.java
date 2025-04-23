package com.decisiontable.ftc.core.drivers.common;

import com.decisiontable.ftc.core.drivers.DTPDriver;
import com.decisiontable.ftc.core.utils.ConfigurationException;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.Map;
import java.util.Objects;

public class GamepadDriver implements DTPDriver {
    private Gamepad gamepad;

    @Override
    public void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions) {
        Object gamepadSelect = Objects.requireNonNull(deviceOptions.getOrDefault("Gamepad", "Gamepad1"));
        if (gamepadSelect.equals("Gamepad1")) {
            gamepad = opMode.gamepad1;
        } else if (gamepadSelect.equals("Gamepad2")) {
            gamepad = opMode.gamepad2;
        } else {
            throw new ConfigurationException("Gamepads should have a Gamepad value of Gamepad1 or Gamepad2. Please check your configuration");
        }
    }

    @Override
    public void set(String param, Object value) {
        // No settable parameters
    }

    @Override
    public Object get(String value) {
        switch (value.toUpperCase()) {
            case "A":
                return gamepad.a;
            case "B":
                return gamepad.b;
            case "X":
                return gamepad.x;
            case "Y":
                return gamepad.y;
                //TODO: Finish this...
            default:
                return null;
        }
    }
}
