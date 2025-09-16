package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensorDriver {

    boolean[] isInitialized;
    int numSensors;
    DistanceSensor[] Sensors;


    public DistanceSensorDriver(int numSensors) {

        this.numSensors = numSensors;
        Sensors = new DistanceSensor[numSensors];

        //for (i = 0; i < numSensors; i++) {
        //   Sensors[i] = new DistanceSensor;
        //}

        int i = 0;
        this.isInitialized = new boolean[numSensors];
        for (i = 0; i < numSensors; i++) {
            isInitialized[i] = false;
        }
    }

    public double get(int channel) {
        if ((channel >= 0) && (channel < numSensors)) {

            return Sensors[channel].getDistance(DistanceUnit.INCH);

        }
        return (-1.0);
    }

    public void set(int channel, double value) {
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized[channel]) {
            Sensors[channel] = hwMap.get(DistanceSensor.class, deviceName);
            isInitialized[channel] = true;
        }
    }
}