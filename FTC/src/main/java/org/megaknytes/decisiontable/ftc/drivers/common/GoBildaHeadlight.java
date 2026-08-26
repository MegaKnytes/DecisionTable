package org.megaknytes.decisiontable.ftc.drivers.common;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class GoBildaHeadlight implements HardwareMapDevice {
    private Servo headlight;
    private Color color = Color.BLACK;

    public enum Color {
        BLACK(0.0),
        RED(0.28),
        ORANGE(0.333),
        YELLOW(0.368),
        SAGE(0.444),
        GREEN(0.5),
        AZURE(0.555),
        BLUE(0.611),
        INDIGO(0.666),
        VIOLET(0.722),
        WHITE(1.0);

        private final double position;

        Color(double position) {
            this.position = position;
        }
    }

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> headlight.getDeviceName(),
                        (name) -> headlight = hardwareMap.get(Servo.class, name));
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Color", Color.class,
                        () -> color,
                        (newColor) -> {
                            color = newColor;
                            headlight.setPosition(color.position);
                        });
    }

    @Override
    public String getDeviceName() {
        return "GoBildaHeadlight";
    }
}