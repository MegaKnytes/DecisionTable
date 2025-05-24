package org.megaknytes.ftc.decisiontable.core.drivers;

import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public interface DTDevice {

    void registerParameters(OpMode opMode, ParameterRegistry registry);

}