package org.megaknytes.ftc.decisiontable.core.drivers.common;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;

public class CRServoDevice implements DTDevice {
    private com.qualcomm.robotcore.hardware.CRServo crServo;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "CRServo", String.class,
                        () -> crServo.getDeviceName(),
                        (crServoName) -> crServo = hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, crServoName))
                .addDependentParameter("Direction", DcMotorSimple.Direction.class,
                        () -> crServo.getDirection(),
                        (direction) -> crServo.setDirection(direction));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Power", Double.class,
                        () -> crServo.getPower(),
                        (crServoPower) -> crServo.setPower(crServoPower))
                .addDependentParameter("Direction", DcMotorSimple.Direction.class,
                        () -> crServo.getDirection(),
                        (direction) -> crServo.setDirection(direction));
    }

    @Override
    public String getDeviceName() {
        return "CRServo";
    }
}