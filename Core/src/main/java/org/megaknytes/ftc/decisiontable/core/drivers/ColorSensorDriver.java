package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

public class ColorSensorDriver {

    private int numSensors;
    private NormalizedColorSensor[] sensors;
    boolean isInitialized[];

    //static DTPLogger log = new DTPLogger();


    // re-ordered for consistency as well as RED is the winning alliance color so it
    // should be first. :-)
    private enum Colors {
        RED,       //0
        BLUE,        //1
        YELLOW      //2
    }

    public ColorSensorDriver(int numSensors) {

        //log.enableDebugLevel();
        this.numSensors = numSensors;
        this.sensors = new NormalizedColorSensor[numSensors];

        int i = 0;
        this.isInitialized = new boolean[numSensors];
        for (i = 0; i < numSensors; i++) {
            isInitialized[i] = false;
        }
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        // i don't think this check will work.  channel number can be 0 which means we have 1 sensor
        // if(channel >= numSensors)
       // {
       //     throw new IllegalArgumentException("ColorSensorDriver init: channel exceeds the number of sensors configured.");
       // }

        // devices could be used multiple times as inputs so we need to check to see if it
        // has already been initialized before doing it again.
        if (!isInitialized[channel]) {
            sensors[channel] = hwMap.get(NormalizedColorSensor.class, deviceName);
            sensors[channel].setGain(100);
            isInitialized[channel] = true;
        }
    }

    /**
     * This method returns the color that the ColorSensor read, as defined by the Colors enum above.
     *  If the color detected does not match any colors in the Colors enum, this method will return -1.0.
     */
    public double get(int channel) {
        //Sensors[channel].enableLed(true); // would this turn on the sensor light?
        //if(channel >= numSensors || channel < 0 || sensors[channel] == null)
        //{
        //    throw new IllegalArgumentException("ColorSensorDriver get: channel is wrong or driver has not initialized properly. Channel=" + channel);
       // }

        if(ColorSensorUtils.isBlue(sensors[channel]))
        {
            //log.debug("ColorSensorDriver returning " + Colors.BLUE.ordinal());
            return Colors.BLUE.ordinal();
        }
        else if(ColorSensorUtils.isRed(sensors[channel]))
        {
            //log.debug("ColorSensorDriver returning " + Colors.RED.ordinal());
            return Colors.RED.ordinal();
        }
        else if(ColorSensorUtils.isYellow(sensors[channel]))
        {
            //log.debug("ColorSensorDriver returning " + Colors.YELLOW.ordinal());
            return Colors.YELLOW.ordinal();
        }
        //log.debug("ColorSensorDriver returning -1");
        return (-1.0); //color unknown
    }

    public void set(int channel, double value)
    {
        //No-op
    }
}