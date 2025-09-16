package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * FanDriver is provided as a simple example of adding a device driver for the decision table processor.
 * Each driver will consist of basically the same functions, however, how you process the inputs provided
 * can make each driver very unique.
 */
public class CRServoDriver {

    // number of IO entries for this driver
    //int numMotorsIO;


    // because the init method is called for each IO entry, we only want the motor to be initialized once.
    // this flag is set to true once the first call to init is complete
    boolean[] isInitialized;

    int numMotors;
    CRServo[] motors;


    public CRServoDriver(int numMotors) {
        this.numMotors = numMotors;
        this.motors = new CRServo[numMotors];

        int i = 0;
        this.isInitialized = new boolean[numMotors];
        for (i = 0; i < numMotors; i++) {
            isInitialized[i] = false;
        }
    }


    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of get we want to use if there is more than one (i.e. getVelocity, getCurrentPosition, etc)
     */

    public double get(int channel) {
        return motors[channel].getPower();
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of set we want to use if there is more than one (i.e. setVelocity, setPower, setTargetPosition)
     */
    public void set(int channel, double value) {
        motors[channel].setPower(value);
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
        if (!isInitialized[channel]) {
            motors[channel] = hwMap.get(CRServo.class, deviceName);
            isInitialized[channel] = true;
        }
    }

}