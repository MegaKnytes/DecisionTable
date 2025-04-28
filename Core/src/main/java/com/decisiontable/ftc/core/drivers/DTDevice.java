package com.decisiontable.ftc.core.drivers;

import com.decisiontable.ftc.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


public interface DTDevice {

    void setup(OpMode opMode, String deviceName);

    void registerParameters(String deviceName, ParameterRegistry registry);

}