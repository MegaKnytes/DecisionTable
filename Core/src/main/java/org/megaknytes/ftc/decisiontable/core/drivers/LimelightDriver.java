package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.megaknytes.ftc.decisiontable.core.DTPLogger;
import org.megaknytes.ftc.decisiontable.core.DecisionTableClass;

public class LimelightDriver {

    private int numSensors;
    private Limelight3A limelight3A;
    private Servo armServo, wristServo;
    boolean isInitialized;
    private int colorStrategy;
    private double[] llpython = new double[8];

    public LimelightDriver(int numSensors, DecisionTableClass.AllianceColor color) {

        this.numSensors = numSensors;
        colorStrategy = 0; //we go for alliance color samples first, can be toggled via channel 0 below.

        if (color == DecisionTableClass.AllianceColor.BLUE) {
            llpython[0] = 1;
        } else if (color == DecisionTableClass.AllianceColor.RED) {
            llpython[0] = 0;
        }

        isInitialized = false;
    }

    public void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        if (!isInitialized) {
            limelight3A = hwMap.get(Limelight3A.class, deviceName);
            armServo = hwMap.get(Servo.class, "intakeArmSwivel");
            wristServo = hwMap.get(Servo.class, "intakeClawRotation");
            limelight3A.updatePythonInputs(llpython);
            limelight3A.pipelineSwitch(0);
            limelight3A.start();
            isInitialized = true;
        }
    }

    /**
     * replace with Limelight stuff
     */
    public double get(int channel) {
        // no-op
        //should we change this to say whether limelight is enabled or not and keep track of that state as well?
        return colorStrategy;
    }

    /*
     *  When channel is zero, it means that the driver toggled the scoring strategy
     *   Value in that case represents what color we are going after based on scoring strategy
     *   0 - alliance color
     *   1 - neutral
     *   2 - either alliance or neutral
     *
     *  When channel is set to one, it means that we are enabling tracking and the automatic
     *   claw positioning by the Limelight camera
     */
    public void set(int channel, double value) {

        if (channel == 0) {
            //Toggle for color strategy
            if(colorStrategy == 1)
                colorStrategy = 0;
            else
                colorStrategy++;
            DTPLogger.info("Limelight driver color toggled to " + colorStrategy + "\n");
            llpython[1] = colorStrategy;
            limelight3A.updatePythonInputs(llpython);
        }
        else if (channel == 1) {
            if (!limelight3A.isConnected()) {
                DTPLogger.error("LimelightDriver::set Limelight is not connected.");
                return;
            }
            LLResult latestPipelineResult = limelight3A.getLatestResult();
            if (latestPipelineResult != null) {
                setIntakeArmSwivelDegrees(-latestPipelineResult.getTx()*1.15);
                setIntakeClawRotationDegrees(latestPipelineResult.getPythonOutput()[0]);
            }
        }
    }

    public void setIntakeArmSwivelDegrees(double degrees) {
        armServo.setPosition((degrees / 360.0) + 0.475);
    }

    /**
     * Helper method to set the position of the intake claw rotation servo in degrees from the horizontal
     * @param degrees - The desired position of the intake claw rotation servo in degrees from the horizontal
     */
    public void setIntakeClawRotationDegrees(double degrees) {
        wristServo.setPosition((degrees / 360.0) + 0.52);
    }
}