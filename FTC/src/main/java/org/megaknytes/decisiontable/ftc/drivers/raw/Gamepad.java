package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.OpModeDevice;

public class Gamepad implements OpModeDevice {
    private com.qualcomm.robotcore.hardware.Gamepad gamepad;

    @Override
    public void registerConfiguration(OpMode opMode, ParameterRegistry registry) {
        registry.createParameter(this, "ID", Integer.class,
                () -> gamepad.id,
                (gamepadID) -> gamepad = gamepadID == 1 ? opMode.gamepad1 : opMode.gamepad2);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameterGroup(this, "Button")
                .addSubParameter("A", Boolean.class, () -> gamepad.a)
                .addSubParameter("B", Boolean.class, () -> gamepad.b)
                .addSubParameter("X", Boolean.class, () -> gamepad.x)
                .addSubParameter("Y", Boolean.class, () -> gamepad.y)
                .addSubParameter("LeftBumper", Boolean.class, () -> gamepad.left_bumper)
                .addSubParameter("RightBumper", Boolean.class, () -> gamepad.right_bumper)
                .addSubParameter("LeftStickPress", Boolean.class, () -> gamepad.left_stick_button)
                .addSubParameter("RightStickPress", Boolean.class, () -> gamepad.right_stick_button)
                .addSubParameter("Start", Boolean.class, () -> gamepad.start)
                .addSubParameter("Back", Boolean.class, () -> gamepad.back);

        registry.createParameterGroup(this, "ButtonPressed")
                .addSubParameter("A", Boolean.class, () -> gamepad.aWasPressed())
                .addSubParameter("B", Boolean.class, () -> gamepad.bWasPressed())
                .addSubParameter("X", Boolean.class, () -> gamepad.xWasPressed())
                .addSubParameter("Y", Boolean.class, () -> gamepad.yWasPressed())
                .addSubParameter("LeftBumper", Boolean.class, () -> gamepad.leftBumperWasPressed())
                .addSubParameter("RightBumper", Boolean.class, () -> gamepad.rightBumperWasPressed())
                .addSubParameter("LeftStickPress", Boolean.class, () -> gamepad.leftStickButtonWasPressed())
                .addSubParameter("RightStickPress", Boolean.class, () -> gamepad.rightStickButtonWasPressed())
                .addSubParameter("Start", Boolean.class, () -> gamepad.startWasPressed())
                .addSubParameter("Back", Boolean.class, () -> gamepad.backWasPressed());

        registry.createParameterGroup(this, "Trigger")
                .addSubParameter("LeftTrigger", Float.class, () -> gamepad.left_trigger)
                .addSubParameter("RightTrigger", Float.class, () -> gamepad.right_trigger);

        registry.createParameterGroup(this, "Joystick")
                .addSubParameter("LeftStickX", Float.class, () -> gamepad.left_stick_x)
                .addSubParameter("LeftStickY", Float.class, () -> gamepad.left_stick_y)
                .addSubParameter("RightStickX", Float.class, () -> gamepad.right_stick_x)
                .addSubParameter("RightStickY", Float.class, () -> gamepad.right_stick_y);
    }

    @Override
    public String getDeviceName() {
        return "Gamepad";
    }
}