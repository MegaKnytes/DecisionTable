package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.utils.UpdatableDevice;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class PIDF implements HardwareMapDevice, UpdatableDevice {
    private double kP = 0.0, kI = 0.0, kD = 0.0, kF = 0.0;
    private double target = 0.0, current = 0.0, output = 0.0;

    private double integralSum = 0.0;
    private double lastError = 0.0;
    private long lastUpdateNanos = 0;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameterGroup(this, "Constants")
                .addSubParameter("kP", Double.class, () -> kP, (v) -> kP = v)
                .addSubParameter("kI", Double.class, () -> kI, (v) -> kI = v)
                .addSubParameter("kD", Double.class, () -> kD, (v) -> kD = v)
                .addSubParameter("kF", Double.class, () -> kF, (v) -> kF = v);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Target", Double.class, () -> target, (v) -> target = v);
        registry.createParameter(this, "Current", Double.class, () -> current, (v) -> current = v);
        registry.createParameter(this, "Output", Double.class, () -> output);
    }

    @Override
    public String getDeviceName() {
        return "PIDF";
    }

    @Override
    public void update() {
        long now = System.nanoTime();
        double dt = lastUpdateNanos == 0 ? 0.0 : (now - lastUpdateNanos) / 1e9;
        lastUpdateNanos = now;

        double error = target - current;
        integralSum += dt > 0 ? error * dt : 0.0;
        double derivative = dt > 0 ? (error - lastError) / dt : 0.0;
        lastError = error;

        output = (kP * error) + (kI * integralSum) + (kD * derivative) + (kF * target);
    }
}