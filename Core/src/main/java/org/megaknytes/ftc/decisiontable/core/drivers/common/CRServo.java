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

        Parameter<String> crServoName = registry.createParameter(this, "HardwareMap", String.class, crServoNameSupplier);
        Parameter<DcMotorSimple.Direction> direction = registry.createParameter(this, "Direction", DcMotorSimple.Direction.class, directionSupplier);
        Parameter<Double> power = registry.createParameter(this, "Power", Double.class, powerSupplier);

        crServoName.addListener(newHardwareMapName -> {
            this.crServoName = newHardwareMapName;
            this.crServo = opMode.hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, newHardwareMapName);
        });

        power.addListener(newPower -> {
            crServo.setPower(newPower);
        });

        direction.addListener(newDirection -> {
            this.direction = newDirection;
            crServo.setDirection(newDirection);
        });
    }
}