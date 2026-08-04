package org.megaknytes.decisiontable.ftc.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.decisiontable.ftc.xml.registry.ParameterRegistry;

public interface DTDeviceEx extends DTDevice {

    void registerConfiguration(OpMode opMode, ParameterRegistry registry);

}