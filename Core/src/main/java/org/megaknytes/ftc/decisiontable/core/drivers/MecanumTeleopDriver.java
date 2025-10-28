package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumTeleopDriver {
    static int NUM_MOTORS = 4;
    /***
     * Adjust defaultPowerMultiplier as well as drivetrainPower values from year to year based on motor RPM and game needs
     *
     * Often "full" power is way too fast and we never want to drive that fast, so we use the multiplier to scale down.
     * For Into the Deep, our "fastest" power will be half of "full power"
     */
    static double defaultPowerMultiplier = 0.5;
    static double minimumDrivetrainPower = 0.3;
    static double maximumDrivetrainPower = 0.5;  // slowed from 0.8 for STEMFest
    int numGamePads;
    Gamepad gp1, gp2;
    DcMotorEx leftFront;
    DcMotorEx leftRear;
    DcMotorEx rightFront;
    DcMotorEx rightRear;

    public MecanumTeleopDriver(int numGamePads) {
        //int i;
        this.numGamePads = numGamePads;
    }

    /**
     * The channel will be used to determine which wheel power to calculate
     * 0 = leftFront
     * 1 = leftRear
     * 2 = rightFront
     * 3 = rightRear
     */
    public double get(int channel)
    {
        /**
         * DO NOT MODIFY THE CODE BELOW! IT IS NEEDED!!
         * Talk to Mrs. Norton or Mrs. Carroll if you don't understand why.
         */
        double powerMultiplier = defaultPowerMultiplier;
        //Adjust speed if slow down button is pressed OR speed up trigger value is significant
        boolean isSlowDownEnabled = (gp1.left_trigger > 0.1); //Depends on controller configuration for given game year
        double turboBoost = gp1.right_trigger; //Depends on controller configuration for given game year
        if(isSlowDownEnabled)
        {
            powerMultiplier = minimumDrivetrainPower;
        }
        else if (turboBoost > 0.1)
        {
            powerMultiplier = ((maximumDrivetrainPower - defaultPowerMultiplier) * turboBoost) + defaultPowerMultiplier;
        }

        double frontLeftPower = (-gp1.left_stick_y + (gp1.left_stick_x * 1.2) + gp1.right_stick_x);
        double backLeftPower = (-gp1.left_stick_y - (gp1.left_stick_x * 1.2) + gp1.right_stick_x);
        double frontRightPower = (-gp1.left_stick_y - (gp1.left_stick_x * 1.2) - gp1.right_stick_x);
        double backRightPower = (-gp1.left_stick_y + (gp1.left_stick_x * 1.2) - gp1.right_stick_x);

        /**
         * Calculate the scaled power value for the specific wheel based on channel number
         * Note: Make sure we never send a value greater than the maximum power value so we don't break the robot
         *  The algorithm should prevent it from happening, but the min function provides extra safety.
         */
         switch (channel) {
            case 0:  // leftFront
                return frontLeftPower * powerMultiplier;
            case 1: // leftRear
                return backLeftPower * powerMultiplier;
            case 2: // rightFront
                return frontRightPower * powerMultiplier;
            case 3: // rightRear
                return backRightPower * powerMultiplier;
            default:
                break;
        }
        return 0.0; //This should be impossible unless channels are not configured correctly.
    }

    /**
     * This method sets power values for all 4 wheels. It intentionally ignores the parameters of channel and value.
     * This method will set all four wheels of the drive train, and calls the get method to take care of the power calculation.
     * @param channel
     * @param value
     */
    public void set(int channel, double value)
    {
        if(leftFront == null || leftRear == null || rightFront == null || rightRear == null)
        {
            System.out.println("One or more drivetrain wheels is not initialized. Make sure the channels are correct!");
        }

        leftFront.setPower(this.get(0));
        leftRear.setPower(this.get(1));
        rightFront.setPower(this.get(2));
        rightRear.setPower(this.get(3));
    }

    public void init(String IOName, int channel, double initVal, String deviceName, Gamepad gp1, Gamepad gp2, HardwareMap hwMap) {
        this.gp1 = gp1;
        this.gp2 = gp2;

        switch (channel) {
            case 0:
                leftFront = hwMap.get(DcMotorEx.class, deviceName);
                leftFront.setDirection(DcMotor.Direction.FORWARD);
                leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                break;
            case 1:
                leftRear = hwMap.get(DcMotorEx.class, deviceName);
                leftRear.setDirection(DcMotor.Direction.FORWARD);
                leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                break;
            case 2:
                rightFront = hwMap.get(DcMotorEx.class, deviceName);
                rightFront.setDirection(DcMotor.Direction.REVERSE );
                rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                break;
            case 3:
                rightRear = hwMap.get(DcMotorEx.class, deviceName);
                rightRear.setDirection(DcMotor.Direction.REVERSE);
                rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                break;
        }
    }
}