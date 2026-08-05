package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class TouchSensor implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.TouchSensor touchSensor;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> touchSensor.getDeviceName(),
                        (name) -> touchSensor = hardwareMap.get(com.qualcomm.robotcore.hardware.TouchSensor.class, name));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Pressed", Boolean.class, () -> touchSensor.isPressed());
    }

    @Override
    public String getDeviceName() {
        return "TouchSensor";
    }
}