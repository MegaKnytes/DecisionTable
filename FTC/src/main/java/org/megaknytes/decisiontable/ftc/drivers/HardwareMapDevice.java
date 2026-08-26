package org.megaknytes.decisiontable.ftc.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;

public interface HardwareMapDevice extends Device {

    void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry);
}