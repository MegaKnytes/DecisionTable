package org.megaknytes.ftc.decisiontable.core.drivers.common;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.Enabled;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@Enabled
public class GamepadDriver implements DTDevice {
    private static final Logger logger = Logger.getLogger(GamepadDriver.class.getName());
    private Gamepad gamepad;

    @Override
    public void setup(OpMode opMode, String deviceName) {
        try {
            if (deviceName == null || deviceName.isEmpty()) {
                throw new IllegalArgumentException("Device name cannot be null or empty");
            }
            if (deviceName.equals("gamepad1")){
                gamepad = opMode.gamepad1;
            } else if (deviceName.equals("gamepad2")){
                gamepad = opMode.gamepad2;
            } else {
                throw new IllegalArgumentException("Invalid device name: " + deviceName);
            }
            logger.log(Level.INFO, "Gamepad initialized: " + deviceName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Gamepad: " + deviceName, e);
            throw new RuntimeException("Failed to initialize Gamepad: " + deviceName, e);
        }
    }

    @Override
    public void registerParameters(String deviceName, ParameterRegistry registry) {
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

        registry.createParameter(deviceName, "a", Boolean.class, aSupplier);
        registry.createParameter(deviceName, "b", Boolean.class, bSupplier);
        registry.createParameter(deviceName, "x", Boolean.class, xSupplier);
        registry.createParameter(deviceName, "y", Boolean.class, ySupplier);
        registry.createParameter(deviceName, "left_bumper", Boolean.class, leftBumperSupplier);
        registry.createParameter(deviceName, "right_bumper", Boolean.class, rightBumperSupplier);
        registry.createParameter(deviceName, "left_stick_button", Boolean.class, leftStickButtonSupplier);
        registry.createParameter(deviceName, "right_stick_button", Boolean.class, rightStickButtonSupplier);
        registry.createParameter(deviceName, "dpad_up", Boolean.class, dpadUpSupplier);
        registry.createParameter(deviceName, "dpad_down", Boolean.class, dpadDownSupplier);
        registry.createParameter(deviceName, "dpad_left", Boolean.class, dpadLeftSupplier);
        registry.createParameter(deviceName, "dpad_right", Boolean.class, dpadRightSupplier);
        registry.createParameter(deviceName, "start", Boolean.class, startSupplier);
        registry.createParameter(deviceName, "back", Boolean.class, backSupplier);
        registry.createParameter(deviceName, "left_trigger", Float.class, leftTriggerSupplier);
        registry.createParameter(deviceName, "right_trigger", Float.class, rightTriggerSupplier);
        registry.createParameter(deviceName, "left_stick_x", Float.class, leftStickXSupplier);
        registry.createParameter(deviceName, "left_stick_y", Float.class, leftStickYSupplier);
        registry.createParameter(deviceName, "right_stick_x", Float.class, rightStickXSupplier);
        registry.createParameter(deviceName, "right_stick_y", Float.class, rightStickYSupplier);
    }
}