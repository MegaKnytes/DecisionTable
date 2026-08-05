package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class DigitalChannel implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.DigitalChannel digitalChannel;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> digitalChannel.getDeviceName(),
                        (name) -> digitalChannel = hardwareMap.get(com.qualcomm.robotcore.hardware.DigitalChannel.class, name))
                .addSubParameter("Mode", com.qualcomm.robotcore.hardware.DigitalChannel.Mode.class,
                        () -> digitalChannel.getMode(),
                        (mode) -> digitalChannel.setMode(mode));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "State", Boolean.class,
                        () -> digitalChannel.getState(),
                        (state) -> digitalChannel.setState(state));
    }

    @Override
    public String getDeviceName() {
        return "DigitalChannel";
    }
}