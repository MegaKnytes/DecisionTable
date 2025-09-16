package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

/**
 * FanDriver is provided as a simple example of adding a device driver for the decision table processor.
 * Each driver will consist of basically the same functions, however, how you process the inputs provided
 * can make each driver very unique.
 */
public class AxonServoDriver {

    // number of IO entries for this driver
    //int numMotorsIO;


    // because the init method is called for each IO entry, we only want the motor to be initialized once.
    // this flag is set to true once the first call to init is complete
    boolean[] isInitialized;

    double currentPos;

    int numMotors;
    ServoImplEx[] motors;

    /**
     * FanDriver Constructor
     * //@param numMotorIO -- number of inputs and outputs for this device
     */
    //public FourMotorSequence(int numMotorIO) {
    //    this.numMotorIO = numMotorIO;
    //}
    public AxonServoDriver(int numMotors) {
        //log.debug("Axon Servo Num: " + numMotors);
        this.numMotors = numMotors;
        this.motors = new ServoImplEx[numMotors];

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

    // TODO:  Not sure this is needed right now
    public double get(int channel) {
        return motors[channel].getPosition();
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of set we want to use if there is more than one (i.e. setVelocity, setPower, setTargetPosition)
     */
    public void set(int channel, double value) {
        if (value == -1){
            motors[channel].setPwmDisable();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            motors[channel].setPwmEnable();
            motors[channel].setPwmRange(new PwmControl.PwmRange(500, 2500));
        } else {
            //log.debug("Set Servo: " + channel + " to value: " + value);
            motors[channel].setPosition(value);
            //log.debug("Get Value from Controller: " + motors[channel].getPosition());
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
        if (!isInitialized[channel]) {
            motors[channel] = hwMap.get(ServoImplEx.class, deviceName);
            motors[channel].setPwmRange(new PwmControl.PwmRange(500, 2500));
            isInitialized[channel] = true;
            currentPos = initVal;
        }
    }

}