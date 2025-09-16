package org.megaknytes.ftc.decisiontable.core.drivers;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

public class ColorSensorUtils {

    //static DTPLogger log = new DTPLogger();

    private ColorSensorUtils() {
        //log.enableDebugLevel();
    } //class cannot be instantiated, helper methods only

    public static boolean isRed(NormalizedColorSensor sensor)
    {
        float[] hsvValues = new float[3];
        //TODO: These values are hard coded for now, we should make them easier to configure.
        Color.colorToHSV(sensor.getNormalizedColors().toColor(), hsvValues);
        //System.out.println("Selected: isRed ColorSensorUtils HSV Values = " + hsvValues[0] + "\n");
        return (hsvValues[0] >= 10 && hsvValues[0] <= 35);
    }

    public static boolean isBlue(NormalizedColorSensor sensor)
    {
        float[] hsvValues = new float[3];
        //TODO: These values are hard coded for now, we should make them easier to configure.
        Color.colorToHSV(sensor.getNormalizedColors().toColor(), hsvValues);
        //log.debug("ColorSensorUtils HSV Values = " + hsvValues[0]+ "\n");
        //System.out.println("Selected: isBlue ColorSensorUtils HSV Values = " + hsvValues[0] + "\n");
        return (hsvValues[0] >= 207 && hsvValues[0] <= 240);
    }

    public static boolean isYellow(NormalizedColorSensor sensor)
    {
        float[] hsvValues = new float[3];
        //TODO: These values are hard coded for now, we should make them easier to configure.
        Color.colorToHSV(sensor.getNormalizedColors().toColor(), hsvValues);
        //log.debug("ColorSensorUtils HSV Values = " + hsvValues[0] + "\n");
        //System.out.println("Selected: isYellow ColorSensorUtils HSV Values = " + hsvValues[0] + "\n");
        return (hsvValues[0] >= 50 && hsvValues[0] <= 90);    }
}