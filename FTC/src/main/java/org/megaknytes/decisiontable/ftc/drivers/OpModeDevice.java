package org.megaknytes.decisiontable.ftc.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.utils.device.Device;

public interface OpModeDevice extends Device {

    void registerConfiguration(OpMode opMode, ParameterRegistry registry);
}