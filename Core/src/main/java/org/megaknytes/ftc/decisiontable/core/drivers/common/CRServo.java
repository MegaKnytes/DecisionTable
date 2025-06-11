package org.megaknytes.ftc.decisiontable.core.drivers.common;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.ParameterRegistry;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class CRServo implements DTDevice {
    private String crServoName;
    private com.qualcomm.robotcore.hardware.CRServo crServo;

    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameterGroup(this, "Configuration")
                .addParameter("Name", String.class, () -> crServoName, (crServoName) -> {
                    this.crServoName = crServoName;
                    crServo = hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, crServoName);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, () -> direction, (direction) -> {
                    this.direction = direction;
                    crServo.setDirection(direction);
                });
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameterGroup(this, "Value")
                .addParameter("Power", Double.class, () -> crServo.getPower(), (power) -> crServo.setPower(power));
    }
}