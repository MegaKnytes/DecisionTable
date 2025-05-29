package org.megaknytes.ftc.decisiontable.core.drivers.common;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.function.Supplier;

public class Gamepad implements DTDevice {
    private Integer id;
    private com.qualcomm.robotcore.hardware.Gamepad gamepad;

    @Override
    public void registerParameters(OpMode opMode, ParameterRegistry registry) {
        Supplier <Integer> gamepadIDSupplier = () -> id;

        Supplier<Boolean> aSupplier = () -> gamepad.a;
        Supplier<Boolean> bSupplier = () -> gamepad.b;
        Supplier<Boolean> xSupplier = () -> gamepad.x;
        Supplier<Boolean> ySupplier = () -> gamepad.y;
        Supplier<Boolean> leftBumperSupplier = () -> gamepad.left_bumper;
        Supplier<Boolean> rightBumperSupplier = () -> gamepad.right_bumper;
        Supplier<Boolean> leftStickButtonSupplier = () -> gamepad.left_stick_button;
        Supplier<Boolean> rightStickButtonSupplier = () -> gamepad.right_stick_button;
        Supplier<Boolean> dpadUpSupplier = () -> gamepad.dpad_up;
        Supplier<Boolean> dpadDownSupplier = () -> gamepad.dpad_down;
        Supplier<Boolean> dpadLeftSupplier = () -> gamepad.dpad_left;
        Supplier<Boolean> dpadRightSupplier = () -> gamepad.dpad_right;
        Supplier<Boolean> startSupplier = () -> gamepad.start;
        Supplier<Boolean> backSupplier = () -> gamepad.back;

        Supplier<Float> leftTriggerSupplier = () -> gamepad.left_trigger;
        Supplier<Float> rightTriggerSupplier = () -> gamepad.right_trigger;
        Supplier<Float> leftStickXSupplier = () -> gamepad.left_stick_x;
        Supplier<Float> leftStickYSupplier = () -> gamepad.left_stick_y;
        Supplier<Float> rightStickXSupplier = () -> gamepad.right_stick_x;
        Supplier<Float> rightStickYSupplier = () -> gamepad.right_stick_y;


        registry.createParameterGroup(this, "Configuration")
                .addParameter("GamepadID", Integer.class, gamepadIDSupplier, (id) -> {
                    this.id = id;
                    this.gamepad = id == 1 ? opMode.gamepad1 : opMode.gamepad2;
                });

        registry.createParameterGroup(this, "Buttons")
                .addParameter("A", Boolean.class, aSupplier, (value) -> gamepad.a = value)
                .addParameter("B", Boolean.class, bSupplier, (value) -> gamepad.b = value)
                .addParameter("X", Boolean.class, xSupplier, (value) -> gamepad.x = value)
                .addParameter("Y", Boolean.class, ySupplier, (value) -> gamepad.y = value)
                .addParameter("LeftBumper", Boolean.class, leftBumperSupplier, (value) -> gamepad.left_bumper = value)
                .addParameter("RightBumper", Boolean.class, rightBumperSupplier, (value) -> gamepad.right_bumper = value)
                .addParameter("LeftStickButton", Boolean.class, leftStickButtonSupplier, (value) -> gamepad.left_stick_button = value)
                .addParameter("RightStickButton", Boolean.class, rightStickButtonSupplier, (value) -> gamepad.right_stick_button = value)
                .addParameter("DPadUp", Boolean.class, dpadUpSupplier, (value) -> gamepad.dpad_up = value)
                .addParameter("DPadDown", Boolean.class, dpadDownSupplier, (value) -> gamepad.dpad_down = value)
                .addParameter("DPadLeft", Boolean.class, dpadLeftSupplier, (value) -> gamepad.dpad_left = value)
                .addParameter("DPadRight", Boolean.class, dpadRightSupplier, (value) -> gamepad.dpad_right = value)
                .addParameter("Start", Boolean.class, startSupplier, (value) -> gamepad.start = value)
                .addParameter("Back", Boolean.class, backSupplier, (value) -> gamepad.back = value);

        registry.createParameterGroup(this, "Triggers")
                .addParameter("LeftTrigger", Float.class, leftTriggerSupplier, (value) -> gamepad.left_trigger = value)
                .addParameter("RightTrigger", Float.class, rightTriggerSupplier, (value) -> gamepad.right_trigger = value);

        registry.createParameterGroup(this, "Sticks")
                .addParameter("LeftStickX", Float.class, leftStickXSupplier, (value) -> gamepad.left_stick_x = value)
                .addParameter("LeftStickY", Float.class, leftStickYSupplier, (value) -> gamepad.left_stick_y = value)
                .addParameter("RightStickX", Float.class, rightStickXSupplier, (value) -> gamepad.right_stick_x = value)
                .addParameter("RightStickY", Float.class, rightStickYSupplier, (value) -> gamepad.right_stick_y = value);
    }
}