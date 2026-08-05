package org.megaknytes.decisiontable.ftc.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.registry.ParameterRegistry;

public interface HardwareMapDevice extends Device {

    void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry);
}
