package org.megaknytes.ftc.decisiontable.core.drivers.common;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.function.Supplier;

public class MecanumDrive implements DTDevice {
    private String frontLeftName, frontRightName, backLeftName, backRightName;
    private DcMotor.Direction frontLeftDirection = FORWARD, frontRightDirection = FORWARD, backLeftDirection = FORWARD,backRightDirection = FORWARD;
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Float x_power = 0.0F, y_power = 0.0F, rx_power = 0.0F, speed = 1.0F;

    @Override
    public void registerParameters(OpMode opMode, ParameterRegistry registry) {
        Supplier<String> frontLeftNameSupplier = () -> frontLeftName;
        Supplier<String> frontRightNameSupplier = () -> frontRightName;
        Supplier<String> backLeftNameSupplier = () -> backLeftName;
        Supplier<String> backRightNameSupplier = () -> backRightName;

        Supplier<DcMotor.Direction> frontLeftDirectionSupplier = () -> frontLeftDirection;
        Supplier<DcMotor.Direction> frontRightDirectionSupplier = () -> frontRightDirection;
        Supplier<DcMotor.Direction> backLeftDirectionSupplier = () -> backLeftDirection;
        Supplier<DcMotor.Direction> backRightDirectionSupplier = () -> backRightDirection;

        Supplier<Float> xSupplier = () -> x_power;
        Supplier<Float> ySupplier = () -> y_power;
        Supplier<Float> rxSupplier = () -> rx_power;

        Supplier<Float> speedModifierSupplier = () -> speed;

        Parameter<String> frontLeftName = registry.createParameter(this, "FrontLeftMotorName", String.class, frontLeftNameSupplier);
        Parameter<String> frontRightName = registry.createParameter(this, "FrontRightMotorName", String.class, frontRightNameSupplier);
        Parameter<String> backLeftName = registry.createParameter(this, "BackLeftMotorName", String.class, backLeftNameSupplier);
        Parameter<String> backRightName = registry.createParameter(this, "BackRightMotorName", String.class, backRightNameSupplier);

        Parameter<DcMotor.Direction> frontLeftDirection = registry.createParameter(this, "FrontLeftMotorDirection", DcMotor.Direction.class, frontLeftDirectionSupplier);
        Parameter<DcMotor.Direction> frontRightDirection = registry.createParameter(this, "FrontRightMotorDirection", DcMotor.Direction.class, frontRightDirectionSupplier);
        Parameter<DcMotor.Direction> backLeftDirection = registry.createParameter(this, "BackLeftMotorDirection", DcMotor.Direction.class, backLeftDirectionSupplier);
        Parameter<DcMotor.Direction> backRightDirection = registry.createParameter(this, "BackRightMotorDirection", DcMotor.Direction.class, backRightDirectionSupplier);

        Parameter<Float> x = registry.createParameter(this, "X", Float.class, xSupplier);
        Parameter<Float> y = registry.createParameter(this, "Y", Float.class, ySupplier);
        Parameter<Float> rx = registry.createParameter(this, "RX", Float.class, rxSupplier);
        Parameter<Float> speedModifier = registry.createParameter(this, "speed", Float.class, speedModifierSupplier);

        frontLeftName.addListener((name) -> {
            this.frontLeftName = name;
            this.frontLeft = opMode.hardwareMap.get(DcMotor.class, name);
        });

        frontRightName.addListener((name) -> {
            this.frontRightName = name;
            this.frontRight = opMode.hardwareMap.get(DcMotor.class, name);
        });

        backLeftName.addListener((name) -> {
            this.backLeftName = name;
            this.backLeft = opMode.hardwareMap.get(DcMotor.class, name);
        });

        backRightName.addListener((name) -> {
            this.backRightName = name;
            this.backRight = opMode.hardwareMap.get(DcMotor.class, name);
        });

        frontLeftDirection.addListener((direction) -> {
            this.frontLeftDirection = direction;
            this.frontLeft.setDirection(direction);
        });

        frontRightDirection.addListener((direction) -> {
            this.frontRightDirection = direction;
            this.frontRight.setDirection(direction);
        });

        backLeftDirection.addListener((direction) -> {
            this.backLeftDirection = direction;
            this.backLeft.setDirection(direction);
        });

        backRightDirection.addListener((direction) -> {
            this.backRightDirection = direction;
            this.backRight.setDirection(direction);
        });

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
