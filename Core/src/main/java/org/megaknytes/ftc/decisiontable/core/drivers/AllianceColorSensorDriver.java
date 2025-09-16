package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.megaknytes.ftc.decisiontable.core.DecisionTableClass;


public class AllianceColorSensorDriver {

    private DecisionTableClass.AllianceColor allianceColor;
    private int numSensors;

    // AKC: removing sensors because there is not one on the robot and it produces errors on init.
    // not sure this is necessary anymore if teleop is setting the value but that's a discussion
    // when things are less busy.

    //private ColorSensor[] sensors;
    //static DTPLogger log = new DTPLogger();


    public AllianceColorSensorDriver(int numSensors, DecisionTableClass.AllianceColor color) {
        //log.enableDebugLevel();
        this.allianceColor = color;
        this.numSensors = numSensors;
        //this.sensors = new ColorSensor[numSensors];
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if(channel >= numSensors)
        {
            throw new IllegalArgumentException("AllianceColorSensorDriver init: channel exceeds the number of sensors configured.");
        }
        //if (sensors[channel] == null) {
        //    sensors[channel] = hwMap.get(ColorSensor.class, deviceName);
        //}
    }

    /**
     * The channel indicates the color sensor (there may be more than 1 per robot).
     *  This method will return:
     *    1.0 if the color read by the sensor matches the alliance color
     *    -1.0 if the color read by the sensor matches the opposite team's alliance color
     *    0 otherwise (any color besides red or blue)
     */
    public double get(int channel) {
        //Sensors[channel].enableLed(true); // would this turn on the sensor light?
        //Channel zero checks for the alliance color (red or blue)
        //if(channel >= numSensors || channel < 0 || sensors[channel] == null)
        if(channel >= numSensors || channel < 0)
        {
            throw new IllegalArgumentException("AllianceColorSensorDriver get: channel is wrong or driver has not initialized properly. Channel=" + channel);
        }

        //if((allianceColor == allianceColor.BLUE && ColorSensorUtils.isBlue(sensors[channel])) ||
        //        (allianceColor == allianceColor.RED && ColorSensorUtils.isRed(sensors[channel])))
        // {
        //        return 1.0; //alliance color detected
        //}
        //else if((allianceColor == allianceColor.BLUE && ColorSensorUtils.isRed(sensors[channel])) ||
        //        (allianceColor == allianceColor.RED && ColorSensorUtils.isBlue(sensors[channel])))
        //{
        //    return -1.0; //opposite team alliance color detected
        //}
        //else
        //{
        //    return 0.0; //some other color
        //}
        //log.debug("Alliance Color = " + (double) allianceColor.ordinal() + "\n");
        return (double) allianceColor.ordinal();
    }

    public void set(int channel, double value)
    {
        //No-op
    }
}