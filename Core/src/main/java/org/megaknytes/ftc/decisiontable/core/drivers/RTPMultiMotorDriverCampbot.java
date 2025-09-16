package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class RTPMultiMotorDriverCampbot {


    /**
     * When using this driver, each RTPMotor must use all 3 motorIOs, even if they never get used
     */
    static final double VELOCITY = 2000.0;


    boolean isInitialized;

    int numMotorIO;
    //int numMotors;

    DcMotorEx motor1, motor2;

    DcMotorEx[] motors;

    /**
     * FanDriver Constructor
     *
     * @param numMotorIO -- number of inputs and outputs for this device
     */
    public RTPMultiMotorDriverCampbot(int numMotorIO) {

        this.numMotorIO = numMotorIO;
        //this.numMotors = (int) (numMotorIO / 3);

        //int i = 0;
        this.isInitialized = false;
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of get we want to use if there is more than one (i.e. getVelocity, getCurrentPosition, etc)
     */
    public double get(int channel) {
        //int whichMotor = (int) (channel / 3);
        if (channel == 0) return motor1.getCurrentPosition();

        else if (channel == 3) return motor2.getCurrentPosition();

        else return 0.0;
        //return motors[channel].getCurrentPosition();
    }

    /**
     * @param channel -- channel could be the physical port of the device but at this point,
     *                we're thinking we could use channel as sort of a case identifier to know
     *                which type of set we want to use if there is more than one (i.e. setVelocity, setPower, setTargetPosition)
     */
    public void set(int channel, double value) {
        //int whichMotor = (int) (channel / 3);
        //int state = (int) (channel % 3);

        switch (channel) {
            case 0: // turn the motor to the specified position
                motor1.setVelocity(VELOCITY);
                motor1.setTargetPosition((int) value);
                break;
            case 1: // reset the motor
                motor1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                break;
            case 2: // turn the motor off
                motor1.setVelocity(0.0);
                break;

            case 3:
                motor2.setVelocity(VELOCITY);
                motor2.setTargetPosition((int) value);
                break;
            case 4:
                motor2.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                break;
            case 5:
                motor2.setVelocity(0.0);
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
            motor1 = hwMap.get(DcMotorEx.class, "lift1");
            motor1.setDirection(DcMotorEx.Direction.REVERSE);
            motor1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            motor1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            motor1.setTargetPosition(motor1.getCurrentPosition());
            motor1.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

            motor2 = hwMap.get(DcMotorEx.class, "lift2");
            motor2.setDirection(DcMotorEx.Direction.REVERSE);
            motor2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            motor2.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            motor2.setTargetPosition(motor2.getCurrentPosition());
            motor2.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

            isInitialized = true;
        }

        /*if (!isInitialized[(int)(channel/3)]) {
            motors[(int)(channel/3)] = hwMap.get(DcMotorEx.class, deviceName);
            motors[(int)(channel/3)].setDirection(DcMotorEx.Direction.REVERSE);
            motors[(int)(channel/3)].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            motors[(int)(channel/3)].setTargetPosition(motors[(int)(channel/3)].getCurrentPosition());
            motors[(int)(channel/3)].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motors[(int)(channel/3)].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            isInitialized[(int)(channel/3)] = true;
        }*/
    }
}