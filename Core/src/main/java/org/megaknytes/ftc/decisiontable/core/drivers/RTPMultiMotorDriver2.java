package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * FanDriver is provided as a simple example of adding a device driver for the decision table processor.
 * Each driver will consist of basically the same functions, however, how you process the inputs provided
 * can make each driver very unique.
 */
public class RTPMultiMotorDriver2 {

    boolean[] isInitialized;

    int numMotorIO;
    int numMotors;
    DcMotorEx[] motors;

    // if set() has setVelocity(), setTolerance(), and setTargetPosition(), then we only
    // need 1 motor for every 3 motor channels
    int numCases = 2;

    /** FanDriver Constructor
     * //@param numMotorIO -- number of inputs and outputs for this device
     */
    //public FourMotorSequence(int numMotorIO) {
    //    this.numMotorIO = numMotorIO;
    //}

    public RTPMultiMotorDriver2(int numMotorIO) {
        this.numMotorIO = numMotorIO;
        this.numMotors = numMotorIO / numCases;
        this.motors = new DcMotorEx[numMotorIO / numCases];

        this.isInitialized = new boolean[this.numMotors];
        for(int i=0; i<this.numMotors; i++) { isInitialized[i] = false; }
    }


    /** @param channel -- channel could be the physical port of the device but at this point,
     *                    we're thinking we could use channel as sort of a case identifier to know
     *                    which type of get we want to use if there is more than one (i.e. getVelocity, getCurrentPosition, etc)
     */

    public double get(int channel) {
        int index = channel / numCases;
        int motor_operation = channel % numCases;

        switch (motor_operation){
            default:
            case 0:
                return motors[index].getCurrentPosition();
            case 1:
                return motors[index].getCurrent(CurrentUnit.AMPS);
        }
    }

    /** @param channel -- channel could be the physical port of the device but at this point,
     *                    we're thinking we could use channel as sort of a case identifier to know
     *                    which type of set we want to use if there is more than one (i.e. setVelocity, setPower, setTargetPosition)
     */


    // experimental switch set
    public void set(int channel, double value) {
        // 0 -> motor[0].setTargetPosition(value), 1 -> motor[0].setVelocity(value), 2 -> motor[0].setTargetPositionTolerance(value)
        // 3 -> motor[1].setTargetPosition(value), 4 -> motor[1].setVelocity(value), 5 -> motor[1].setTargetPositionTolerance(value)
        // 0 % 3 --> 0, 0/3 --> 0   3 % 3 --> 0, 3/3 --> 1
        // 1 % 3 --> 1, 1/3 --> 0   4 % 3 --> 1, 4/3 --> 1
        // 1 % 3 --> 2, 2/3 --> 0   5 % 3 --> 2, 5/3 --> 1

        int index = channel / numCases;
        int motor_operation = channel % numCases;

        switch (motor_operation){
            case 0:
                motors[index].setTargetPosition((int) value);
                break;
            case 1:
                motors[index].setPower(value);
                break;
        }
    }

    /**
     * Initializes the device(s) for this device driver.
     * @param IOName
     * @param channel - could be used to identify how the motor should be initialized.
     * @param initVal
     * @param deviceName
     * @param hwMap
     */

    //EXPERIMENTAL init for switch of set

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        int index = channel / numCases;
        if (!isInitialized[index]) {
            motors[index] = hwMap.get(DcMotorEx.class, deviceName);
            //motors[index].setDirection(DcMotorEx.Direction.REVERSE);
            motors[index].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            motors[index].setTargetPosition(motors[index].getCurrentPosition());
            motors[index].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motors[index].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            motors[index].setVelocity(2000);
            isInitialized[index] = true;
        }
    }



/**
    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized[channel]) {
            motors[channel] = hwMap.get(DcMotorEx.class, deviceName);
            motors[channel].setDirection(DcMotorEx.Direction.REVERSE);
            motors[channel].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            motors[channel].setTargetPosition(motors[channel].getCurrentPosition());
            motors[channel].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motors[channel].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            isInitialized[channel] = true;
        }
    }
   */

}