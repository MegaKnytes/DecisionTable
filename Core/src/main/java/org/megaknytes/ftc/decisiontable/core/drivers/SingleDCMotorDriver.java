package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * FanDriver is provided as a simple example of adding a device driver for the decision table processor.
 * Each driver will consist of basically the same functions, however, how you process the inputs provided
 * can make each driver very unique.
 */
public class SingleDCMotorDriver {

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
    public SingleDCMotorDriver(int numMotorIO) {
        this.numMotorIO = numMotorIO;
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of get we want to use if there is more than one (i.e. getVelocity, getCurrentPosition, etc)
     */
    public double get(int channel) {
        switch (channel) {
            case 0:
                return motor.getVelocity();
            case 1:
                return motor.getCurrent(CurrentUnit.AMPS);
            default:
                return 0;
        }
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of set we want to use if there is more than one (i.e. setVelocity, setPower, setTargetPosition)
     */
    public void set(int channel, double value) {
        motor.setPower(value);
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
            isInitialized = true;
        }
    }
}