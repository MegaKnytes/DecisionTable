package org.megaknytes.ftc.decisiontable.core.drivers.common;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.ParameterRegistry;

public class Servo implements DTDevice {
    private String servoName;
    private com.qualcomm.robotcore.hardware.Servo servo;
    private com.qualcomm.robotcore.hardware.Servo.Direction direction = com.qualcomm.robotcore.hardware.Servo.Direction.FORWARD;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameterGroup(this, "Configuration")
                .addParameter("Name", String.class, () -> servoName, (servoName) -> {
                    this.servoName = servoName;
                    servo = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, servoName);
                })
                .addParameter("Direction", com.qualcomm.robotcore.hardware.Servo.Direction.class, () -> direction, (direction) -> {
                    this.direction = direction;
                    servo.setDirection(direction);
                });
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameterGroup(this, "Value")
                .addParameter("Position", Double.class, () -> servo.getPosition(), (position) -> servo.setPosition(position));
    }
}