package org.megaknytes.decisiontable.ftc.drivers.raw;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.utils.device.UpdatableDevice;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class ColorSensor implements HardwareMapDevice, UpdatableDevice {
    private com.qualcomm.robotcore.hardware.NormalizedColorSensor colorSensor;

    private double hueMin = 0.0, hueMax = 255.0;
    private double saturationMin = 0.0, saturationMax = 1.0;
    private double valueMin = 0.0, valueMax = 1.0;

    private double red, green, blue, alpha;
    private double hue, saturation, value;
    private boolean detected;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> colorSensor.getDeviceName(),
                        (name) -> colorSensor = hardwareMap.get(com.qualcomm.robotcore.hardware.NormalizedColorSensor.class, name));

        registry.createParameter(this, "Gain", Float.class,
                        () -> colorSensor.getGain(),
                        (gain) -> colorSensor.setGain(gain));

        registry.createParameter(this, "HueMin", Double.class, () -> hueMin, (v) -> hueMin = v);
        registry.createParameter(this, "HueMax", Double.class, () -> hueMax, (v) -> hueMax = v);
        registry.createParameter(this, "SaturationMin", Double.class, () -> saturationMin, (v) -> saturationMin = v);
        registry.createParameter(this, "SaturationMax", Double.class, () -> saturationMax, (v) -> saturationMax = v);
        registry.createParameter(this, "ValueMin", Double.class, () -> valueMin, (v) -> valueMin = v);
        registry.createParameter(this, "ValueMax", Double.class, () -> valueMax, (v) -> valueMax = v);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Red", Double.class, () -> red);
        registry.createParameter(this, "Green", Double.class, () -> green);
        registry.createParameter(this, "Blue", Double.class, () -> blue);
        registry.createParameter(this, "Alpha", Double.class, () -> alpha);

        registry.createParameter(this, "Hue", Double.class, () -> hue);
        registry.createParameter(this, "Saturation", Double.class, () -> saturation);
        registry.createParameter(this, "Value", Double.class, () -> value);

        registry.createParameter(this, "Detected", Boolean.class, () -> detected);
    }

    @Override
    public String getDeviceName() {
        return "ColorSensor";
    }

    @Override
    public void update() {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        red = colors.red;
        green = colors.green;
        blue = colors.blue;
        alpha = colors.alpha;

        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];

        detected = hue >= hueMin && hue <= hueMax && saturation >= saturationMin && saturation <= saturationMax && value >= valueMin && value <= valueMax;
    }
}