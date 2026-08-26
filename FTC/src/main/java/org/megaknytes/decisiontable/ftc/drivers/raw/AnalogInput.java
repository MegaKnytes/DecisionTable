package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class AnalogInput implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.AnalogInput analogInput;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> analogInput.getDeviceName(),
                        (name) -> analogInput = hardwareMap.get(com.qualcomm.robotcore.hardware.AnalogInput.class, name));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Voltage", Double.class, () -> analogInput.getVoltage());
    }

    @Override
    public String getDeviceName() {
        return "AnalogInput";
    }
}