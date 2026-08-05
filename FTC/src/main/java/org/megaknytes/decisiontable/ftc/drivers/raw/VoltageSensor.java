package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class VoltageSensor implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.VoltageSensor voltageSensor;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> voltageSensor.getDeviceName(),
                        (name) -> voltageSensor = hardwareMap.voltageSensor.iterator().next());
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Voltage", Double.class, () -> voltageSensor.getVoltage());
    }

    @Override
    public String getDeviceName() {
        return "VoltageSensor";
    }
}