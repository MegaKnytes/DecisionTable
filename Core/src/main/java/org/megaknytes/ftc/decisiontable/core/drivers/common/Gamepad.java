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
                .addParameter("A",                Boolean.class, aSupplier)
                .addParameter("B",                Boolean.class, bSupplier)
                .addParameter("X",                Boolean.class, xSupplier)
                .addParameter("Y",                Boolean.class, ySupplier)
                .addParameter("LeftBumper",       Boolean.class, leftBumperSupplier)
                .addParameter("RightBumper",      Boolean.class, rightBumperSupplier)
                .addParameter("LeftStickButton",  Boolean.class, leftStickButtonSupplier)
                .addParameter("RightStickButton", Boolean.class, rightStickButtonSupplier)
                .addParameter("DPadUp",           Boolean.class, dpadUpSupplier)
                .addParameter("DPadDown",         Boolean.class, dpadDownSupplier)
                .addParameter("DPadLeft",         Boolean.class, dpadLeftSupplier)
                .addParameter("DPadRight",        Boolean.class, dpadRightSupplier)
                .addParameter("Start",            Boolean.class, startSupplier)
                .addParameter("Back",             Boolean.class, backSupplier);

        registry.createParameterGroup(this, "Triggers")
                .addParameter("LeftTrigger",  Float.class, leftTriggerSupplier)
                .addParameter("RightTrigger", Float.class, rightTriggerSupplier);

        registry.createParameterGroup(this, "Sticks")
                .addParameter("LeftStickX",  Float.class, leftStickXSupplier)
                .addParameter("LeftStickY",  Float.class, leftStickYSupplier)
                .addParameter("RightStickX", Float.class, rightStickXSupplier)
                .addParameter("RightStickY", Float.class, rightStickYSupplier);
    }
}