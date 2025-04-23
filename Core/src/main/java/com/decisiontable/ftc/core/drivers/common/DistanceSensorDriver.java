package com.decisiontable.ftc.core.drivers.common;

import com.decisiontable.ftc.core.drivers.DTPDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Map;
import java.util.Objects;


public class DistanceSensorDriver implements DTPDriver {
    private DistanceSensor sensor;
    private DistanceUnit unit;

    @Override
    public void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions) {
        sensor = opMode.hardwareMap.get(DistanceSensor.class, deviceName);
        switch ((DistanceUnit) Objects.requireNonNull(deviceOptions.getOrDefault("DISTANCE_UNIT", DistanceUnit.INCH))) {
            case MM:
                unit = DistanceUnit.MM;
                break;
            case METER:
                unit = DistanceUnit.METER;
                break;
            case CM:
                unit = DistanceUnit.CM;
                break;
            case INCH:
            default:
                unit = DistanceUnit.INCH;
                break;
        }
    }


    @Override
    public void set(String param, Object value) {
        // No settable parameters
    }

    @Override
    public Object get(String value) {
        if (value.toUpperCase().equals("DISTANCE")) {
            return sensor.getDistance(unit);
        } else {
            return null;
        }
    }
}
