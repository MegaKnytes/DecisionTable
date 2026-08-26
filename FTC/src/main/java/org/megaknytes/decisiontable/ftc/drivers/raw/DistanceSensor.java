package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class DistanceSensor implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.DistanceSensor distanceSensor;
    private DistanceUnit distanceUnit = DistanceUnit.MM;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> distanceSensor.getDeviceName(),
                        (name) -> distanceSensor = hardwareMap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class, name))
                .addSubParameter("DistanceUnit", DistanceUnit.class,
                        () -> distanceUnit,
                        (unit) -> {
                            distanceUnit = unit;
                        });
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Distance", Double.class, () -> distanceSensor.getDistance(distanceUnit));
    }

    @Override
    public String getDeviceName() {
        return "DistanceSensor";
    }
}