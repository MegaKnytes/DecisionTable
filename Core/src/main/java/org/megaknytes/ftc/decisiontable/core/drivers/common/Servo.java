package org.megaknytes.ftc.decisiontable.core.drivers.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;

import java.util.function.Supplier;

public class Servo implements DTDevice {
    private String servoName;
    private com.qualcomm.robotcore.hardware.Servo servo;

    @Override
    public void registerParameters(OpMode opMode, ParameterRegistry registry) {
        Supplier<String> servoNameSupplier = () -> servoName;
        Supplier<Double> positionSupplier = () -> servo.getPosition();

        Parameter<String> servoName = registry.createParameter(this, "HardwareMap", String.class, servoNameSupplier);
        Parameter<Double> position = registry.createParameter(this, "Power", Double.class, positionSupplier);

        servoName.addListener(newHardwareMapName -> {
            this.servoName = newHardwareMapName;
            this.servo = opMode.hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, newHardwareMapName);
        });

        position.addListener(newPosition -> {
            servo.setPosition(newPosition);
        });
    }
}