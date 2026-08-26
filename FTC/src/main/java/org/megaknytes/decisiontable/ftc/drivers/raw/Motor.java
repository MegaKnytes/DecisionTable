package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class Motor implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.DcMotorEx dcMotor;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> dcMotor.getDeviceName(),
                        (motorName) -> dcMotor = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotorEx.class, motorName))
                .addSubParameter("Direction", com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.class,
                        () -> dcMotor.getDirection(),
                        (direction) -> dcMotor.setDirection(direction));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "ZeroPowerBehavior", com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.class,
                        () -> dcMotor.getZeroPowerBehavior(),
                        (zeroPowerBehavior) -> dcMotor.setZeroPowerBehavior(zeroPowerBehavior));
        registry.createParameter(this, "Mode", com.qualcomm.robotcore.hardware.DcMotor.RunMode.class,
                        () -> dcMotor.getMode(),
                        (mode) -> dcMotor.setMode(mode));
        registry.createParameter(this, "Power", Double.class,
                        () -> dcMotor.getPower(),
                        (power) -> dcMotor.setPower(power));
        registry.createParameter(this, "Velocity", Double.class,
                        () -> dcMotor.getVelocity(),
                        (velocity) -> dcMotor.setVelocity(velocity));
        registry.createParameter(this, "Position", Integer.class,
                        () -> dcMotor.getCurrentPosition(),
                        (position) -> dcMotor.setTargetPosition(position));
    }

    @Override
    public String getDeviceName() {
        return "Motor";
    }
}