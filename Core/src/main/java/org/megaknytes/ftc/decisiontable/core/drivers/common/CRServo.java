package org.megaknytes.ftc.decisiontable.core.drivers.common;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.function.Supplier;

public class CRServo implements DTDevice {
    private String crServoName;
    private com.qualcomm.robotcore.hardware.CRServo crServo;

    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;

    @Override
    public void registerParameters(OpMode opMode, ParameterRegistry registry) {
        Supplier<String> crServoNameSupplier = () -> crServoName;
        Supplier<DcMotorSimple.Direction> directionSupplier = () -> direction;
        Supplier<Double> powerSupplier = () -> crServo.getPower();

        registry.createParameterGroup(this, "Configuration")
                .addParameter("ServoName", String.class, crServoNameSupplier, (name) -> {
                    this.crServoName = name;
                    this.crServo = opMode.hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, name);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, directionSupplier, (dir) -> {
                    this.direction = dir;
                    this.crServo.setDirection(dir);
                });

        registry.createParameterGroup(this, "Value")
                .addParameter("Power", Double.class, powerSupplier, (power) -> crServo.setPower(power));
    }
}