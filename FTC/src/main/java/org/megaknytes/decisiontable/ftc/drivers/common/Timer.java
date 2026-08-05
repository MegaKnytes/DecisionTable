package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.OpModeDevice;

public class Timer implements OpModeDevice {
    private final ElapsedTime elapsedTime = new ElapsedTime();

    @Override
    public void registerConfiguration(OpMode opMode, ParameterRegistry registry) {
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "ElapsedSeconds", Double.class, elapsedTime::seconds);
        registry.createParameter(this, "Reset", Boolean.class, () -> false, (trigger) -> elapsedTime.reset());
    }

    @Override
    public String getDeviceName() {
        return "Timer";
    }
}