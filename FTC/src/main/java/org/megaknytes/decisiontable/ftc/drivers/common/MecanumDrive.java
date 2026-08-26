package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.utils.device.InitializedDevice;
import org.megaknytes.decisiontable.core.utils.device.UpdatableDevice;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class MecanumDrive implements HardwareMapDevice, UpdatableDevice, InitializedDevice {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Float x_power = 0.0f, y_power = 0.0f, rx_power = 0.0f, scale = 1.0f;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "FrontLeft", String.class, () -> frontLeft.getDeviceName(), (frontLeftName) -> frontLeft = hardwareMap.get(DcMotor.class, frontLeftName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class, () -> frontLeft.getDirection(), (frontLeftDirection) -> frontLeft.setDirection(frontLeftDirection));

        registry.createParameter(this, "FrontRight", String.class, () -> frontRight.getDeviceName(), (frontRightName) -> frontRight = hardwareMap.get(DcMotor.class, frontRightName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class, () -> frontRight.getDirection(), (frontRightDirection) -> frontRight.setDirection(frontRightDirection));

        registry.createParameter(this, "BackLeft", String.class, () -> backLeft.getDeviceName(), (backLeftName) -> backLeft = hardwareMap.get(DcMotor.class, backLeftName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class, () -> backLeft.getDirection(), (backLeftDirection) -> backLeft.setDirection(backLeftDirection));

        registry.createParameter(this, "BackRight", String.class, () -> backRight.getDeviceName(), (backRightName) -> backRight = hardwareMap.get(DcMotor.class, backRightName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class, () -> backRight.getDirection(), (backRightDirection) -> backRight.setDirection(backRightDirection));
    }

    @Override
    public void initialize() {
        assert frontLeft != null : "Front Left motor is not configured.";
        assert frontRight != null : "Front Right motor is not configured.";
        assert backLeft != null : "Back Left motor is not configured.";
        assert backRight != null : "Back Right motor is not configured.";

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Scale", Float.class, () -> scale, (speed) -> this.scale = speed);
        registry.createParameter(this, "X", Float.class, () -> x_power, (x_power) -> this.x_power = x_power);
        registry.createParameter(this, "Y", Float.class, () -> y_power, (y_power) -> this.y_power = y_power);
        registry.createParameter(this, "RX", Float.class, () -> rx_power, (rx_power) -> this.rx_power = rx_power);
    }

    @Override
    public String getDeviceName() {
        return "MecanumDrive";
    }

    @Override
    public void update() {
        double denominator = Math.max(Math.abs(y_power) + Math.abs(x_power) + Math.abs(rx_power), 1);
        double frontLeftPower = ((y_power + x_power + rx_power) / denominator) * scale;
        double backLeftPower = ((y_power - x_power + rx_power) / denominator) * scale;
        double frontRightPower = ((y_power - x_power - rx_power) / denominator) * scale;
        double backRightPower = ((y_power + x_power - rx_power) / denominator) * scale;

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}