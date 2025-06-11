package org.megaknytes.ftc.decisiontable.core.drivers.common;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.function.Supplier;

public class MecanumDrive implements DTDevice {
    private String frontLeftName, frontRightName, backLeftName, backRightName;
    private DcMotor.Direction frontLeftDirection = FORWARD, frontRightDirection = FORWARD, backLeftDirection = FORWARD,backRightDirection = FORWARD;
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Float x_power = 0.0F, y_power = 0.0F, rx_power = 0.0F, speed = 1.0F;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameterGroup(this, "FrontLeft")
                .addParameter("MotorName", String.class, () -> frontLeftName, (frontLeftName) -> {
                    this.frontLeftName = frontLeftName;
                    frontLeft = hardwareMap.get(DcMotor.class, frontLeftName);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, () -> frontLeftDirection, (frontLeftDirection) -> {
                    this.frontLeftDirection = frontLeftDirection;
                    frontLeft.setDirection(frontLeftDirection);
                });

        registry.createParameterGroup(this, "FrontRight")
                .addParameter("MotorName", String.class, () -> frontRightName, (frontRightName) -> {
                    this.frontRightName = frontRightName;
                    frontRight = hardwareMap.get(DcMotor.class, frontRightName);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, () -> frontRightDirection, (frontRightDirection) -> {
                    this.frontRightDirection = frontRightDirection;
                    frontRight.setDirection(frontRightDirection);
                });

        registry.createParameterGroup(this, "BackLeft")
                .addParameter("MotorName", String.class, () -> backLeftName, (backLeftName) -> {
                    this.backLeftName = backLeftName;
                    backLeft = hardwareMap.get(DcMotor.class, backLeftName);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, () -> backLeftDirection, (backLeftDirection) -> {
                    this.backLeftDirection = backLeftDirection;
                    backLeft.setDirection(backLeftDirection);
                });

        registry.createParameterGroup(this, "BackRight")
                .addParameter("MotorName", String.class, () -> backRightName, (backRightName) -> {
                    this.backRightName = backRightName;
                    backRight = hardwareMap.get(DcMotor.class, backRightName);
                })
                .addParameter("Direction", DcMotorSimple.Direction.class, () -> backRightDirection, (backRightDirection) -> {
                    this.backRightDirection = backRightDirection;
                    backRight.setDirection(backRightDirection);
                });
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameterGroup(this, "DrivePower")
                .addParameter("X", Float.class, () -> x_power, (x_power) -> {
                    this.x_power = x_power;
                    update();
                })
                .addParameter("Y", Float.class, () -> y_power, (y_power) -> {
                    this.y_power = y_power;
                    update();
                })
                .addParameter("RX", Float.class, () -> rx_power, (rx_power) -> {
                    this.rx_power = rx_power;
                    update();
                })
                .addParameter("Speed", Float.class, () -> speed, (speed) -> {
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
