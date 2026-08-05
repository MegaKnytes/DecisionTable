package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class CRServo implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.CRServo crServo;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> crServo.getDeviceName(),
                        (crServoName) -> crServo = hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, crServoName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class,
                        () -> crServo.getDirection(),
                        (direction) -> crServo.setDirection(direction));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Power", Double.class,
                        () -> crServo.getPower(),
                        (crServoPower) -> crServo.setPower(crServoPower));
    }

    @Override
    public String getDeviceName() {
        return "CRServo";
    }
}