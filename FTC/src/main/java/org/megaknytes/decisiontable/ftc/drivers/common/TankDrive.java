package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.utils.UpdatableDevice;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class TankDrive implements HardwareMapDevice, UpdatableDevice {
    private DcMotor left, right;
    private Float y_power = 0.0f, rx_power = 0.0f, scale = 1.0f;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "Left", String.class, () -> left.getDeviceName(), (leftName) -> left = hardwareMap.get(DcMotor.class, leftName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class, () -> left.getDirection(), (direction) -> left.setDirection(direction));

        registry.createParameter(this, "Right", String.class, () -> right.getDeviceName(), (rightName) -> right = hardwareMap.get(DcMotor.class, rightName))
                .addSubParameter("Direction", DcMotorSimple.Direction.class, () -> right.getDirection(), (direction) -> right.setDirection(direction));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Scale", Float.class, () -> scale, (v) -> this.scale = v);
        registry.createParameter(this, "Y", Float.class, () -> y_power, (v) -> this.y_power = v);
        registry.createParameter(this, "RX", Float.class, () -> rx_power, (v) -> this.rx_power = v);
    }

    @Override
    public String getDeviceName() {
        return "TankDrive";
    }

    @Override
    public void update() {
        double denominator = Math.max(Math.abs(y_power) + Math.abs(rx_power), 1);
        double leftPower = ((y_power + rx_power) / denominator) * scale;
        double rightPower = ((y_power - rx_power) / denominator) * scale;

        left.setPower(leftPower);
        right.setPower(rightPower);
    }
}