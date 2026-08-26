package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.OpModeDevice;

public class Alliance implements OpModeDevice {
    AllianceColor allianceColor;

    @Override
    public void registerConfiguration(OpMode opMode, ParameterRegistry registry) {
        registry.createParameter(this, "Color", AllianceColor.class,
                () -> allianceColor);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Color", AllianceColor.class, () -> allianceColor);
    }

    @Override
    public String getDeviceName() {
        return "Alliance";
    }

    public enum AllianceColor {
        BLUE,
        RED
    }
}