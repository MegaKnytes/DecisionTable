package org.megaknytes.ftc.decisiontable.core.drivers.common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;

public class MecanumDriveDevice implements DTDevice {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Float x_power = 0.0F, y_power = 0.0F, rx_power = 0.0F, speed = 1.0F;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "FrontLeft", String.class, () -> frontLeft.getDeviceName(), (frontLeftName) -> {
                    frontLeft = hardwareMap.get(DcMotor.class, frontLeftName);
                })
                .addDependentParameter("Direction", DcMotorSimple.Direction.class, () -> frontLeft.getDirection(), (frontLeftDirection) -> {
                    frontLeft.setDirection(frontLeftDirection);
                });

        registry.createParameter(this, "FrontRight", String.class, () -> frontRight.getDeviceName(), (frontRightName) -> {
                    frontRight = hardwareMap.get(DcMotor.class, frontRightName);
                })
                .addDependentParameter("Direction", DcMotorSimple.Direction.class, () -> frontRight.getDirection(), (frontRightDirection) -> {
                    frontRight.setDirection(frontRightDirection);
                });

        registry.createParameter(this, "BackLeft", String.class, () -> backLeft.getDeviceName(), (backLeftName) -> {
                    backLeft = hardwareMap.get(DcMotor.class, backLeftName);
                })
                .addDependentParameter("Direction", DcMotorSimple.Direction.class, () -> backLeft.getDirection(), (backLeftDirection) -> {
                    backLeft.setDirection(backLeftDirection);
                });

        registry.createParameter(this, "BackRight", String.class, () -> backRight.getDeviceName(), (backRightName) -> {
                    backRight = hardwareMap.get(DcMotor.class, backRightName);
                })
                .addDependentParameter("Direction", DcMotorSimple.Direction.class, () -> backRight.getDirection(), (backRightDirection) -> {
                    backRight.setDirection(backRightDirection);
                });
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "DrivePower")
                .addDependentParameter("X", Float.class, () -> x_power, (x_power) -> {
                    this.x_power = x_power;
                    update();
                })
                .addDependentParameter("Y", Float.class, () -> y_power, (y_power) -> {
                    this.y_power = y_power;
                    update();
                })
                .addDependentParameter("RX", Float.class, () -> rx_power, (rx_power) -> {
                    this.rx_power = rx_power;
                    update();
                })
                .addDependentParameter("Speed", Float.class, () -> speed, (speed) -> {
                    this.speed = speed;
                    update();
                });
    }

    @Override
    public String getDeviceName() {
        return "MecanumDrive";
    }

    public void update() {
        double denominator = Math.max(Math.abs(y_power) + Math.abs(x_power) + Math.abs(rx_power), 1);
        double frontLeftPower = ((y_power + x_power + rx_power) / denominator) * speed;
        double backLeftPower = ((y_power - x_power + rx_power) / denominator) * speed;
        double frontRightPower = ((y_power - x_power - rx_power) / denominator) * speed;
        double backRightPower = ((y_power + x_power - rx_power) / denominator) * speed;

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}