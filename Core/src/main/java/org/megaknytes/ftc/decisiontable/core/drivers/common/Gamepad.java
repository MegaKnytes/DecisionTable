package org.megaknytes.ftc.decisiontable.core.drivers.common;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceExtended;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Gamepad implements DTDeviceExtended {
    private Integer id;
    private com.qualcomm.robotcore.hardware.Gamepad gamepad;

    @Override
    public void registerConfiguration(OpMode opMode, ParameterRegistry registry) {
        registry.createParameterGroup(this, "Configuration")
                .addParameter("ID", Integer.class, () -> id, (id) -> {
                    this.id = id;
                    this.gamepad = id == 1 ? opMode.gamepad1 : opMode.gamepad2;
                });
    }

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        // NO-OP: Gamepad does not require hardware map configuration
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameterGroup(this, "Buttons")
                .addParameter("A",               Boolean.class, () -> gamepad.a)
                .addParameter("B",               Boolean.class, () -> gamepad.b)
                .addParameter("X",               Boolean.class, () -> gamepad.x)
                .addParameter("Y",               Boolean.class, () -> gamepad.y)
                .addParameter("LeftBumper",      Boolean.class, () -> gamepad.left_bumper)
                .addParameter("RightBumper",     Boolean.class, () -> gamepad.right_bumper)
                .addParameter("LeftStickPress",  Boolean.class, () -> gamepad.left_stick_button)
                .addParameter("RightStickPress", Boolean.class, () -> gamepad.right_stick_button)
                .addParameter("Start",           Boolean.class, () -> gamepad.start)
                .addParameter("Back",            Boolean.class, () -> gamepad.back);

        registry.createParameterGroup(this, "Triggers")
                .addParameter("LeftTrigger",  Float.class, () -> gamepad.left_trigger)
                .addParameter("RightTrigger", Float.class, () -> gamepad.right_trigger);

        registry.createParameterGroup(this, "Joysticks")
                .addParameter("LeftStickX",  Float.class, () -> gamepad.left_stick_x)
                .addParameter("LeftStickY",  Float.class, () -> gamepad.left_stick_y)
                .addParameter("RightStickX", Float.class, () -> gamepad.right_stick_x)
                .addParameter("RightStickY", Float.class, () -> gamepad.right_stick_y);
    }
}