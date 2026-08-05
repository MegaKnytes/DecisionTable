package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.OpModeDevice;

public class Clock implements OpModeDevice {
    private OpMode opMode;

    @Override
    public void registerConfiguration(OpMode opMode, ParameterRegistry registry) {
        this.opMode = opMode;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "ElapsedSeconds", Double.class, () -> opMode.getRuntime());
    }

    @Override
    public String getDeviceName() {
        return "Clock";
    }
}