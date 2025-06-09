package org.megaknytes.ftc.decisiontable.core.drivers.common;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

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

        registry.createParameterGroup(this, "FrontLeft")
                .addParameter("MotorName", String.class, frontLeftNameSupplier, (name) -> {
                    this.frontLeftName = name;
                    this.frontLeft = opMode.hardwareMap.get(DcMotor.class, name);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, frontLeftDirectionSupplier, (direction) -> {
                    this.frontLeftDirection = direction;
                    this.frontLeft.setDirection(direction);
                });

        registry.createParameterGroup(this, "FrontRight")
                .addParameter("MotorName", String.class, frontRightNameSupplier, (name) -> {
                    this.frontRightName = name;
                    this.frontRight = opMode.hardwareMap.get(DcMotor.class, name);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, frontRightDirectionSupplier, (direction) -> {
                    this.frontRightDirection = direction;
                    this.frontRight.setDirection(direction);
                });

        registry.createParameterGroup(this, "BackLeft")
                .addParameter("MotorName", String.class, backLeftNameSupplier, (name) -> {
                    this.backLeftName = name;
                    this.backLeft = opMode.hardwareMap.get(DcMotor.class, name);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, backLeftDirectionSupplier, (direction) -> {
                    this.backLeftDirection = direction;
                    this.backLeft.setDirection(direction);
                });

        registry.createParameterGroup(this, "BackRight")
                .addParameter("MotorName", String.class, backRightNameSupplier, (name) -> {
                    this.backRightName = name;
                    this.backRight = opMode.hardwareMap.get(DcMotor.class, name);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, backRightDirectionSupplier, (direction) -> {
                    this.backRightDirection = direction;
                    this.backRight.setDirection(direction);
                });

        registry.createParameterGroup(this, "DrivePowers")
                .addParameter("X", Float.class, xSupplier, (x_power) -> {
                    this.x_power = x_power;
                    update();
                })
                .addParameter("Y", Float.class, ySupplier, (y_power) -> {
                    this.y_power = y_power;
                    update();
                })
                .addParameter("RX", Float.class, rxSupplier, (rx_power) -> {
                    this.rx_power = rx_power;
                    update();
                })
                .addParameter("Speed", Float.class, speedModifierSupplier, (speed) -> {
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
