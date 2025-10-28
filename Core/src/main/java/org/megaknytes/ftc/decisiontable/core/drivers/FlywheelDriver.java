package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.megaknytes.ftc.decisiontable.core.utilities.Flywheel;

import dev.nextftc.ftc.Gamepads;


public class FlywheelDriver {

    int numGamePads;
    Gamepad gp1, gp2;
    Flywheel flywheels = null;

    public FlywheelDriver(int numGamePads) {
        //int i;
        this.numGamePads = numGamePads;
    }

    /**
     * @return flywheel velocity
     */
    public double get(int channel)
    {
        return flywheels.getVelocity();
    }

    /**
     * This method sets power values for all 4 wheels. It intentionally ignores the parameters of channel and value.
     * This method will set all four wheels of the drive train, and calls the get method to take care of the power calculation.
     * @param channel -- not used
     * @param value -- velocity in degrees per second
     */
    public void set(int channel, double value)
    {
        if (value == 0.0) {
            flywheels.stopFlywheel();
        } else {
            flywheels.setTargetVelocity(value);
        }
    }

    public void init(String IOName, int channel, double initVal, String deviceName, Gamepad gp1, Gamepad gp2, HardwareMap hwMap) {
        // leaving in gamepads until we know for sure they aren't needed - akc 10-27-2025
        this.gp1 = gp1;
        this.gp2 = gp2;

        flywheels = new Flywheel();
    }
}