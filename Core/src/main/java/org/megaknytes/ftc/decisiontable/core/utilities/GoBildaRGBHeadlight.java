package org.megaknytes.ftc.decisiontable.core.utilities;

import com.qualcomm.robotcore.hardware.ControlSystem;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.configuration.ServoFlavor;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.ServoType;

import java.util.Objects;

@ServoType(flavor = ServoFlavor.CUSTOM)
@DeviceProperties(xmlTag = "GoBildaRGBHeadlight", name = "GoBilda RGB Indicator Light", description = "https://www.gobilda.com/rgb-indicator-light-pwm-controlled/",
        compatibleControlSystems = ControlSystem.REV_HUB)
public class GoBildaRGBHeadlight implements HardwareDevice {
    private final ServoControllerEx controller;
    private final int port;
    private double ledPosition = 0.0;
    private int mirrorLightPort;
    private COLOR currentColor = COLOR.BLACK;

    public GoBildaRGBHeadlight(ServoControllerEx controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    public enum MODE{
        MIRROR,
        SOLID,
    }

    public enum COLOR {
        BLACK,
        RED,
        ORANGE,
        YELLOW,
        SAGE,
        GREEN,
        AZURE,
        BLUE,
        INDIGO,
        VIOLET,
        WHITE,
    }

    public void setMode(MODE mode){
        if (Objects.isNull(mirrorLightPort) && mode == MODE.MIRROR){
            throw new IllegalArgumentException("No Mirror Light Provided");
        }
        if (Objects.isNull(currentColor) && mode == MODE.SOLID){
            throw new IllegalArgumentException("No Color Provided");
        }
    }

    public void setMirrorLight(GoBildaRGBHeadlight headlight){
        mirrorLightPort = headlight.getPort();
    }

    /**
     * setColor - A function to set the color of the GoBilda Headlight
     * @param color The desired color that you wish to set; One of type COLOR
     */
    public void setColor(COLOR color){
        currentColor = color;
        switch (color){
            case BLACK:
                ledPosition = 0.0;
                break;
            case RED:
                ledPosition = 0.28;
                break;
            case ORANGE:
                ledPosition = 0.333;
                break;
            case YELLOW:
                ledPosition = .368;
                break;
            case SAGE:
                ledPosition = .444;
                break;
            case GREEN:
                ledPosition = .5;
                break;
            case AZURE:
                ledPosition = .555;
                break;
            case BLUE:
                ledPosition = .611;
                break;
            case INDIGO:
                ledPosition = .666;
                break;
            case VIOLET:
                ledPosition = .722;
                break;
            case WHITE:
                ledPosition = 1;
                break;
        }
        controller.setServoPosition(port, ledPosition);
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "GoBilda RGB Indicator Light (PWM Controlled)";
    }

    @Override
    public String getConnectionInfo() {
        return controller.getConnectionInfo() + "; port " + port;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
    }

    @Override
    public void close() {
    }

    private int getPort(){
        return port;
    }
}