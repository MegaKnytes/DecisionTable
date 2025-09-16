package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Documentation for setting up the REV LED Indicator can be found here:
 * https://docs.revrobotics.com/rev-31-2010/application-examples
 */

public class LEDDriver {

    boolean[] isInitialized;
    int numIndicators;
    DigitalChannel[] indicators;

    public LEDDriver(int numIndicators) {

        this.numIndicators = numIndicators;
        indicators = new DigitalChannel[numIndicators];

        this.isInitialized = new boolean[numIndicators];
        for(int i = 0; i < numIndicators; i++){
            isInitialized[i] = false;
        }
    }

    // currently no need for a get function
    public double get(int channel) {
        return (0.0);
        //return (indicators[channel].getState() ? 0.0 : 1.0);
    }

    public void set(int channel, double value) {
        indicators[channel].setState(value == 0.0);
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized[channel]) {
            indicators[channel] = hwMap.get(DigitalChannel.class, deviceName);
            indicators[channel].setMode(DigitalChannel.Mode.OUTPUT);
            indicators[channel].setState(true); // light was starting on at start of TeleOp
            isInitialized[channel] = true;
        }
    }

}