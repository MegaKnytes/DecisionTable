package org.megaknytes.ftc.decisiontable.core.drivers.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceEx;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;

public class GamepadDevice implements DTDeviceEx {
    private com.qualcomm.robotcore.hardware.Gamepad gamepad;

    @Override
    public void registerConfiguration(OpMode opMode, ParameterRegistry registry) {
        registry.createParameter(this, "Gamepad", Integer.class,
                () -> gamepad.id,
                (gamepadID) -> gamepad = gamepadID == 1 ? opMode.gamepad1 : opMode.gamepad2);
    }

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        // NO-OP: Gamepad does not require hardware map configuration
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Buttons")
                .addDependentParameter("A", Boolean.class, () -> gamepad.a)
                .addDependentParameter("B", Boolean.class, () -> gamepad.b)
                .addDependentParameter("X", Boolean.class, () -> gamepad.x)
                .addDependentParameter("Y", Boolean.class, () -> gamepad.y)
                .addDependentParameter("LeftBumper", Boolean.class, () -> gamepad.left_bumper)
                .addDependentParameter("RightBumper", Boolean.class, () -> gamepad.right_bumper)
                .addDependentParameter("LeftStickPress", Boolean.class, () -> gamepad.left_stick_button)
                .addDependentParameter("RightStickPress", Boolean.class, () -> gamepad.right_stick_button)
                .addDependentParameter("Start", Boolean.class, () -> gamepad.start)
                .addDependentParameter("Back", Boolean.class, () -> gamepad.back);

        registry.createParameter(this, "Triggers")
                .addDependentParameter("LeftTrigger", Float.class, () -> gamepad.left_trigger)
                .addDependentParameter("RightTrigger", Float.class, () -> gamepad.right_trigger);

        registry.createParameter(this, "Joysticks")
                .addDependentParameter("LeftStickX", Float.class, () -> gamepad.left_stick_x)
                .addDependentParameter("LeftStickY", Float.class, () -> gamepad.left_stick_y)
                .addDependentParameter("RightStickX", Float.class, () -> gamepad.right_stick_x)
                .addDependentParameter("RightStickY", Float.class, () -> gamepad.right_stick_y);
    }

    @Override
    public String getDeviceName() {
        return "Gamepad";
    }
}