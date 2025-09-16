package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SmartIntakeSensorDriver {

    boolean[] isInitialized;
    int numSensors;
    ColorSensor[] Sensors;

    public SmartIntakeSensorDriver(int numSensors) {
        this.numSensors = numSensors;
        this.Sensors = new ColorSensor[numSensors];
        this.isInitialized = new boolean[numSensors];
    }

    // TODO: find actual pixel argb values
    //int[][] targetColors = {{255, 255, 255}, {0, 255, 0}, {255, 0, 255}, {255, 255, 0}}; // white, green, purple, yellow
    int[][] targetColors = {{124, 116, 154, 102}, {111, 110, 137, 88}, {104, 97, 133, 82}, {107, 100, 134, 89}};

    // why does everything look green?
    /*
           |           No light, no cover           |          yes light, no cover            |
           +--------------------+-------------------+--------------------+--------------------+
           |   side view ARGB   |   top view ARGB   |   side view ARGB   |   top view ARGB    |
    -------+--------------------+-------------------+--------------------+--------------------+
     White | 124, 116, 154, 102 | 113, 109, 141, 91 | 300, 240, 372, 289 | 240, 191, 296, 234 |
    -------+--------------------+-------------------+--------------------+--------------------+
    Yellow | 111, 110, 137, 88  |  77,  87, 100, 44 | 187, 169, 236, 158 | 179, 173, 240, 125 |
    -------+--------------------+-------------------+--------------------+--------------------+
     Green | 104,  97, 133, 82  |  60,  54,  82, 46 | 238, 192, 302, 220 | 127,  97, 172, 114 |
    -------+--------------------+-------------------+--------------------+--------------------+
    Purple | 107, 100, 134, 89  |  75,  72,  87, 68 | 223, 186, 275, 209 | 134, 107, 156, 139 |
    -------+--------------------+-------------------+--------------------+--------------------+
     */

    // .argb() -> (0..255?) combined color value
    // .alpha() / .green() / .blue() / .red() -> (0..255) ARGB value
    int count = 0;
    public double get(int channel) {
        if ((channel >= 0) && (channel < numSensors)) {
            //Sensors[channel].enableLed(true); // turn on the sensor LED

            int[] seenColor = { Sensors[channel].alpha(), Sensors[channel].red(), Sensors[channel].green(), Sensors[channel].blue() };

            if (count % 50 == 0) {
                System.out.println("Color Sensor ARGB: " + seenColor[0] + ", " + seenColor[1] + ", " + seenColor[2] + ", " + seenColor[3]);
                count = 0;
            } count++;

            return isPixelColor(seenColor, 40);
        }
        return (-1.0);
    }

    //  with light: 132, 119, 164, 114
    //  wo/  light: 102,  98, 126,  83
    // difference :  30,  23,  38,  31
    // % diff decr: 227, 193, 232, 272 | mean: 231, deviation: -4, -38, 1, 41 -> 21.0
    // % diff incr: 294, 235, 302, 373 | mean: 301, deviation: -7, -66, 1, 72 -> 36.5

    // for each color, check if total difference between argb values < tolerance
    public double isPixelColor(int[] seenColor, int tolerance) {
        for (int[] color: targetColors) {
            int colorDiff = 0;
            for (int i=0; i<seenColor.length; i++) {
                colorDiff += Math.abs(color[i] - seenColor[i]);
            }

            if (colorDiff <= tolerance) { return (1.0); }
        }
        return (0.0);
    }

    public void set(int channel, double value) {}

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized[channel]) {
            Sensors[channel] = hwMap.get(ColorSensor.class, deviceName);
            Sensors[channel].enableLed(false);
            isInitialized[channel] = true;
        }
    }
}