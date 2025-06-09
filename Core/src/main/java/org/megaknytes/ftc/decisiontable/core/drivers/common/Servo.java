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

        registry.createParameterGroup(this, "Configuration")
                .addParameter("ServoName", String.class, servoNameSupplier, (name) -> {
                    this.servoName = name;
                    this.servo = opMode.hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, name);
                });

        registry.createParameterGroup(this, "Value")
                .addParameter("Position", Double.class, positionSupplier, (position) -> {
                    servo.setPosition(position);
                });
    }
}