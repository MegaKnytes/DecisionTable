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

        Parameter<Integer> gamepadID = registry.createParameter(this, "GamepadID", Integer.class, gamepadIDSupplier);

        registry.createParameter(this, "a", Boolean.class, aSupplier);
        registry.createParameter(this, "b", Boolean.class, bSupplier);
        registry.createParameter(this, "x", Boolean.class, xSupplier);
        registry.createParameter(this, "y", Boolean.class, ySupplier);
        registry.createParameter(this, "left_bumper", Boolean.class, leftBumperSupplier);
        registry.createParameter(this, "right_bumper", Boolean.class, rightBumperSupplier);
        registry.createParameter(this, "left_stick_button", Boolean.class, leftStickButtonSupplier);
        registry.createParameter(this, "right_stick_button", Boolean.class, rightStickButtonSupplier);
        registry.createParameter(this, "dpad_up", Boolean.class, dpadUpSupplier);
        registry.createParameter(this, "dpad_down", Boolean.class, dpadDownSupplier);
        registry.createParameter(this, "dpad_left", Boolean.class, dpadLeftSupplier);
        registry.createParameter(this, "dpad_right", Boolean.class, dpadRightSupplier);
        registry.createParameter(this, "start", Boolean.class, startSupplier);
        registry.createParameter(this, "back", Boolean.class, backSupplier);

        registry.createParameter(this, "left_trigger", Float.class, leftTriggerSupplier);
        registry.createParameter(this, "right_trigger", Float.class, rightTriggerSupplier);
        registry.createParameter(this, "left_stick_x", Float.class, leftStickXSupplier);
        registry.createParameter(this, "left_stick_y", Float.class, leftStickYSupplier);
        registry.createParameter(this, "right_stick_x", Float.class, rightStickXSupplier);
        registry.createParameter(this, "right_stick_y", Float.class, rightStickYSupplier);

        gamepadID.addListener(id -> {
            this.id = id;
            this.gamepad = id == 1 ? opMode.gamepad1 : opMode.gamepad2;
        });
    }
}