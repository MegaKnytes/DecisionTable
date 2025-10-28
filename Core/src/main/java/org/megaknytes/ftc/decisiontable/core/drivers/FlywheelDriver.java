package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlywheelDriver {

    int numFlywheelMotors;
    DcMotorEx[] flywheelMotors;
    Gamepad gp1, gp2;

    public FlywheelDriver(int numFlywheelMotors) {
        this.numFlywheelMotors = numFlywheelMotors;
    }

    /**
     * @return The velocity of the leading flywheel motor in revolutions per minute (RPM)
     */
    public double get(int channel)
    {
        return ticksToRpm(flywheelMotors[0].getVelocity());
    }

    /**
     * This method sets power values for all motors. It intentionally ignores the parameters of channel
     * @param channel -- NoOp
     * @param value -- velocity in revolutions per minute
     */
    public void set(int channel, double value) {
        for(int i = 0; i < numFlywheelMotors; i++) {
            flywheelMotors[i].setVelocity(rpmToTicks(value));
        }
    }

    public void init(String IOName, int channel, double initVal, String deviceName, Gamepad gp1, Gamepad gp2, HardwareMap hwMap) {
        this.gp1 = gp1;
        this.gp2 = gp2;

        if(flywheelMotors == null) {
            flywheelMotors = new DcMotorEx[numFlywheelMotors];
        }

        flywheelMotors[channel] = hwMap.get(DcMotorEx.class, deviceName);

        // Requires that all flywheel motors have an encoder plugged into them so that output is consistent
        flywheelMotors[channel].setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (initVal == 0){
            flywheelMotors[channel].setDirection(DcMotorEx.Direction.FORWARD);
        } else {
            flywheelMotors[channel].setDirection(DcMotorEx.Direction.REVERSE);
        }
    }

    private static double rpmToTicks(double rpm) {
        return (rpm / 60) * 28;
    }

    private static double ticksToRpm(double ticks) {
        return (ticks / 28) * 60;
    }
}