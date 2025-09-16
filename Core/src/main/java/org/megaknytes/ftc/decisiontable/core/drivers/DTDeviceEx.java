package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;

public interface DTDeviceEx extends DTDevice {

    void registerConfiguration(OpMode opMode, ParameterRegistry registry);

}