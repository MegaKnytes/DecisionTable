package com.decisiontable.ftc.core.drivers.common;

import com.decisiontable.ftc.core.drivers.DTDevice;
import com.decisiontable.ftc.core.drivers.Enabled;
import com.decisiontable.ftc.core.xml.parameters.Parameter;
import com.decisiontable.ftc.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.function.Supplier;

@Enabled
public class MecanumDriver implements DTDevice {
    private Float x_power = 0.0F, y_power = 0.0F, rx_power = 0.0F, speed = 1.0F;
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    @Override
    public void setup(OpMode opMode, String deviceName) {
        frontLeft = opMode.hardwareMap.get(DcMotor.class, "front_left");
        frontRight = opMode.hardwareMap.get(DcMotor.class, "front_right");
        backLeft = opMode.hardwareMap.get(DcMotor.class, "back_left");
        backRight = opMode.hardwareMap.get(DcMotor.class, "back_right");
    }

    @Override
    public void registerParameters(String deviceName, ParameterRegistry registry) {
        Supplier<Float> xSupplier = () -> x_power;
        Supplier<Float> ySupplier = () -> y_power;
        Supplier<Float> rxSupplier = () -> rx_power;
        Supplier<Float> speedModifierSupplier = () -> speed;

        Parameter<Float> x = registry.createParameter(deviceName, "x", Float.class, xSupplier);
        Parameter<Float> y = registry.createParameter(deviceName, "y", Float.class, ySupplier);
        Parameter<Float> rx = registry.createParameter(deviceName, "rx", Float.class, rxSupplier);
        Parameter<Float> speedModifier = registry.createParameter(deviceName, "speed", Float.class, speedModifierSupplier);

        x.addListener((x_power) -> {
            this.x_power = x_power;
            update();
        });

        y.addListener((y_power) -> {
            this.y_power = y_power;
            update();
        });

        rx.addListener((rx_power) -> {
            this.rx_power = rx_power;
            update();
        });

        speedModifier.addListener((speed) -> {
            this.speed = speed;
            update();
        });
    }

    public void update(){
        double denominator = Math.max(Math.abs(y_power) + Math.abs(x_power) + Math.abs(rx_power), 1);
        double frontLeftPower  = ((y_power + x_power + rx_power) / denominator) * speed;
        double backLeftPower   = ((y_power - x_power + rx_power) / denominator) * speed;
        double frontRightPower = ((y_power - x_power - rx_power) / denominator) * speed;
        double backRightPower  = ((y_power + x_power - rx_power) / denominator) * speed;

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}
