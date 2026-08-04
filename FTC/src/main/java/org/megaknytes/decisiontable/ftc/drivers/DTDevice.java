package org.megaknytes.decisiontable.ftc.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.ftc.xml.registry.ParameterRegistry;

public interface DTDevice {

    void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry);

    void registerParameters(ParameterRegistry registry);

    String getDeviceName();
}