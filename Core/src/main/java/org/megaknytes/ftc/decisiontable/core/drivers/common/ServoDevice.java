package org.megaknytes.ftc.decisiontable.core.drivers.common;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;

public class ServoDevice implements DTDevice {
    private Servo servo;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "Servo", String.class,
                        () -> servo.getDeviceName(),
                        (servoName) -> servo = hardwareMap.get(Servo.class, servoName))
                .addDependentParameter("Direction", Servo.Direction.class,
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