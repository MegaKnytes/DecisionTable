package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class Servo implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.Servo servo;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> servo.getDeviceName(),
                        (servoName) -> servo = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, servoName))
                .addSubParameter("Direction", com.qualcomm.robotcore.hardware.Servo.Direction.class,
                        () -> servo.getDirection(),
                        (direction) -> servo.setDirection(direction));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Position", Double.class,
                () -> servo.getPosition(),
                (position) -> servo.setPosition(position));
    }

    @Override
    public String getDeviceName() {
        return "Servo";
    }
}