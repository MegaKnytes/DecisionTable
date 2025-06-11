package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;

public interface DTDeviceExtended extends DTDevice {

    void registerConfiguration(OpMode opMode, ParameterRegistry registry);

}