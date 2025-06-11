package org.megaknytes.ftc.decisiontable.core.drivers;

import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.hardware.HardwareMap;

public interface DTDevice {

    void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry);
    void registerParameters(ParameterRegistry registry);

}