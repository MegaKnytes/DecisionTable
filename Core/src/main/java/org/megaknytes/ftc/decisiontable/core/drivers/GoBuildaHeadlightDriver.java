package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.utilities.GoBildaRGBHeadlight;


public class GoBuildaHeadlightDriver {

    boolean[] isInitialized;
    int numHeadlights;
    GoBildaRGBHeadlight[] headlight;

    public GoBuildaHeadlightDriver(int numDevices) {

        //log.enableDebugLevel();
        this.numHeadlights = numDevices;
        headlight = new GoBildaRGBHeadlight[numDevices];

        this.isInitialized = new boolean[numDevices];
        for(int i = 0; i < numDevices; i++){
            isInitialized[i] = false;
        }
    }

    // currently no need for a get function
    public double get(int channel) {
        return (0.0);
    }

    public void set(int channel, double value) {
        //log.debug("Setting LIMELIGHT color to " + (int) value + "\n");
        switch ((int) value) {
            case 0:
                //This is turning the headlight off!!!
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.BLACK);
                break;
            case 1:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.RED);
                break;
            case 2:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.ORANGE);
                break;
            case 3:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.YELLOW);
                break;
            case 4:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.SAGE);
                break;
            case 5:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.GREEN);
                break;
            case 6:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.AZURE);
                break;
            case 7:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.BLUE);
                break;
            case 8:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.INDIGO);
                break;
            case 9:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.VIOLET);
                break;
            case 10:
                headlight[channel].setColor(GoBildaRGBHeadlight.COLOR.WHITE);
                break;

        }
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized[channel]) {
            headlight[channel] = hwMap.get(GoBildaRGBHeadlight.class, deviceName);
            headlight[channel].setMode(GoBildaRGBHeadlight.MODE.SOLID);
            isInitialized[channel] = true;
        }
    }

}