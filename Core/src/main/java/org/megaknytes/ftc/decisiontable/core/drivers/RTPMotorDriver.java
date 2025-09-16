package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * FTPMotorDriver is provided as a simple example of adding a device driver for the decision table processor.
 * This driver will initialize a DCMotor in RUN_TO_POSITION mode.
 */
public class RTPMotorDriver {

    static final double VELOCITY = 2000.0;

    // number of IO entries for this driver
    int numMotorIO;

    // because the init method is called for each IO entry, we only want the motor to be initialized once.
    // this flag is set to true once the first call to init is complete
    boolean isInitialized = false;

    // This driver will control a generic DC motor.
    DcMotorEx motor;

    /**
     * FanDriver Constructor
     *
     * @param numMotorIO -- number of inputs and outputs for this device
     */
    public RTPMotorDriver(int numMotorIO) {
        this.numMotorIO = numMotorIO;
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of get we want to use if there is more than one (i.e. getVelocity, getCurrentPosition, etc)
     */
    public double get(int channel) {
        return motor.getCurrentPosition();
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of set we want to use if there is more than one (i.e. setVelocity, setPower, setTargetPosition)
     */
    public void set(int channel, double value) {
        switch (channel) {
            case 0: // turn the motor to the specified position
                motor.setVelocity(VELOCITY);
                motor.setTargetPosition((int) value);
                break;
            case 1: // reset the motor
                motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                break;
            case 2: // turn the motor off
                motor.setVelocity(0.0);
                break;
        }
    }

    /**
     * Initializes the device(s) for this device driver.
     *
     * @param IOName
     * @param channel    - could be used to identify how the motor should be initialized.
     * @param initVal
     * @param deviceName
     * @param hwMap
     */
    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized) {
            motor = hwMap.get(DcMotorEx.class, deviceName);
            motor.setDirection(DcMotorEx.Direction.REVERSE);
            motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            motor.setTargetPosition(motor.getCurrentPosition());
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            isInitialized = true;
        }
    }
}