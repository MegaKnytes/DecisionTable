package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


public class ChamberDistanceDriver {

    boolean[] isInitialized;
    int numDrivers;
    DistanceSensor leftDistanceSensor, rightDistanceSensor;


    private final double SCORING_DISTANCE = 13.0;
    private final double SCORING_RANGE = 4.0;

    public ChamberDistanceDriver(int numDrivers) {

        this.numDrivers = numDrivers;

        this.isInitialized = new boolean[numDrivers];
        for (int i = 0; i < numDrivers; i++) {
            isInitialized[i] = false;
        }
    }

    public double get(int channel) {
        switch (channel) {
            default:
            case 0:
                if (Math.abs(leftDistanceSensor.getDistance(DistanceUnit.CM) - SCORING_DISTANCE) < SCORING_RANGE ||
                        Math.abs(rightDistanceSensor.getDistance(DistanceUnit.CM) - SCORING_DISTANCE) < SCORING_RANGE) {
                    return 1.0;
                } else if (Math.abs(rightDistanceSensor.getDistance(DistanceUnit.CM)) > 6000) {
                    return 1.0;
                } else {
                    return 0.0;
                }
        }

    }

    public void set(int channel, double value) {
        //No-Op
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized[channel]) {
            leftDistanceSensor = hwMap.get(DistanceSensor.class, "leftChamberDistance");
            rightDistanceSensor = hwMap.get(DistanceSensor.class, "rightChamberDistance");
            isInitialized[channel] = true;
        }
    }

}