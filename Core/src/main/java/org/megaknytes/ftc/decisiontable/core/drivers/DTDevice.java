package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;

public interface DTDevice {

    void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry);

    void registerParameters(ParameterRegistry registry);

    String getDeviceName();
}