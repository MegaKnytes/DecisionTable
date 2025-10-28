package org.megaknytes.ftc.decisiontable.core;


import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;


import org.megaknytes.ftc.decisiontable.core.drivers.AllianceColorSensorDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.AxonServoDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.CRServoDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.ChamberDistanceDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.ColorSensorDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.DistanceSensorDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.GamepadDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.GoBuildaHeadlightDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.KnyteVisionDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.LEDDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.LimelightDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.MecanumTeleopDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.MultiMotorDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.RTPMultiMotorDriver2;
import org.megaknytes.ftc.decisiontable.core.drivers.ServoDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.SingleDCMotorDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.SmartIntakeSensorDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.FlywheelDriver;
import org.megaknytes.ftc.decisiontable.core.drivers.TimerClass;

import java.util.Arrays;


class IORegistryClass {
    String IOName;
    int driver;
    int channel;
    double initVal;
    String deviceName;
}
class DTDriverRegistryClass {
    // TODO:  ADDING DRIVER:  Create a variable for your new driver (i.e. SingleDCMotorDriver singleDCMotorDriver)
    IORegistryClass IORegistry[];
    InternalVariableClass InternalVariableDriver;
    GamepadDriver gamePadDriver;
    TimerClass TimerDriver;
    SingleDCMotorDriver singleDCMotorDriver;
    MultiMotorDriver multiMotorDriver;
    CRServoDriver crServoDriver;
    //RTPMotorDriver rtpMotorDriver;
    RTPMultiMotorDriver2 rtpMotorDriver;
    ServoDriver servoDriver;
    AxonServoDriver axonDriver;
    MecanumTeleopDriver mecanumTeleopDriver;
    DistanceSensorDriver distanceSensorDriver;
    KnyteVisionDriver knyteVisionDriver;
    SmartIntakeSensorDriver intakeSensorDriver;
    LEDDriver ledDriver;
    ColorSensorDriver colorSensorDriver;
    AllianceColorSensorDriver allianceSensorDriver;
    GoBuildaHeadlightDriver goBuildaHeadlightDriver;
    DecisionTableClass.AllianceColor allianceColor;
    ChamberDistanceDriver chamberDistanceDriver;
    LimelightDriver limelightDriver;
    FlywheelDriver flywheelDriver;

    // TODO:  ADDING DRIVER: Add int to hold number of IO definitions for your device driver (i.e. numDCMotors)
    int numRegistered=0, numInternals, numGamepads, numTimers, numDCMotors, numMotors, numCrServos, numRtpMotors,
            numServos, numAxonServos, numMecanum, numDistanceSensor, numKnyteVision, numIntakeSensors, numLED, numColorSensors,
            numAllianceColorSensors, numGoBuildaHeadlights, numChamberDistance, numLimelight, numFlywheel, numIODefs, numInputs, numOutputs, inputList[], outputList[];

    // TODO:  ADDING DRIVER: add IO counter to parameter list for constructor (i.e. int numDCMotors)
    public DTDriverRegistryClass(int numInternals, int numGamepads, int numTimers, int numDCMotors, int numMotors,
                                 int numCrServos, int numRtpMotors, int numServos, int numAxonServos, int numMecanum, int numDistanceSensor,
                                 int numKnyteVision, int numIntakeSensors, int numLED, int numColorSensors, int numAllianceColorSensors,
                                 int numGoBuildaHeadlights, int numChamberDistance, int numLimelight, int numFlywheel, int numInputs, int numOutputs, DecisionTableClass.AllianceColor allianceColor) {
        int i,c;

        this.numInternals = numInternals;
        this.numGamepads = numGamepads;
        this.numTimers = numTimers;

        // TODO:  ADDING DRIVER: assign count to local variable (i.e. this.numDCMotors = numDCMotors)
        this.numDCMotors = numDCMotors;
        this.numMotors = numMotors;
        this.numCrServos = numCrServos;
        this.numRtpMotors = numRtpMotors;
        this.numServos = numServos;
        this.numAxonServos = numAxonServos;
        this.numMecanum = numMecanum;
        this.numDistanceSensor = numDistanceSensor;
        this.numKnyteVision = numKnyteVision;
        this.numIntakeSensors = numIntakeSensors;
        this.numLED = numLED;
        this.numColorSensors = numColorSensors;
        this.numAllianceColorSensors = numAllianceColorSensors;
        this.numGoBuildaHeadlights = numGoBuildaHeadlights;
        this.numChamberDistance = numChamberDistance;
        this.numLimelight = numLimelight;
        this.numFlywheel = numFlywheel;

        // TODO:  ADDING DRIVER: add number of IO definitions for new driver to the total count (see numDCMotors below)
        this.numIODefs = numInternals +
                numGamepads +
                numTimers +
                numDCMotors +
                numMotors +
                numCrServos +
                numRtpMotors +
                numServos +
                numAxonServos +
                numMecanum +
                numDistanceSensor +
                numKnyteVision +
                numIntakeSensors +
                numLED +
                numColorSensors +
                numAllianceColorSensors +
                numGoBuildaHeadlights +
                numChamberDistance +
                numLimelight +
                numFlywheel;

        this.numInputs=numInputs;
        this.numOutputs=numOutputs;

        gamePadDriver = new GamepadDriver(numGamepads);
        TimerDriver = new TimerClass(numTimers);
        InternalVariableDriver = new InternalVariableClass(numInternals);

        // TODO:  ADDING DRIVER: Instantiate the new driver
        singleDCMotorDriver = new SingleDCMotorDriver(numDCMotors);
        multiMotorDriver = new MultiMotorDriver(numMotors);
        crServoDriver = new CRServoDriver(numCrServos);
        //rtpMotorDriver = new RTPMotorDriver(numRtpMotors);
        rtpMotorDriver = new RTPMultiMotorDriver2(numRtpMotors);
        servoDriver = new ServoDriver(numServos);
        axonDriver = new AxonServoDriver(numAxonServos);
        mecanumTeleopDriver = new MecanumTeleopDriver(numMecanum);
        distanceSensorDriver = new DistanceSensorDriver(numDistanceSensor);
        knyteVisionDriver = new KnyteVisionDriver(numKnyteVision);
        intakeSensorDriver = new SmartIntakeSensorDriver(numIntakeSensors);
        ledDriver = new LEDDriver(numLED);
        colorSensorDriver = new ColorSensorDriver(numColorSensors);
        allianceSensorDriver = new AllianceColorSensorDriver(numAllianceColorSensors, allianceColor);
        goBuildaHeadlightDriver = new GoBuildaHeadlightDriver(numGoBuildaHeadlights);
        chamberDistanceDriver = new ChamberDistanceDriver(numChamberDistance);
        limelightDriver = new LimelightDriver(numLimelight, allianceColor);
        flywheelDriver = new FlywheelDriver(numFlywheel);

        IORegistry=new IORegistryClass[numIODefs];
        inputList=new int[numInputs];
        Arrays.fill(inputList, -1);
        outputList=new int[numOutputs];
        Arrays.fill(outputList, -1);

        for (i=0, c=0; c<numInternals; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=0;
        }
        for (c=0; c<numGamepads; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=1;
        }
        for (c=0; c<numTimers; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=2;
        }

        // TODO:  ADDING DRIVER: Create the IORegistry for the number of IO definitions you specified
        //        The driver number is just incremented (i.e. the next driver added would be #4
        for (c=0; c<numDCMotors; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=3;
        }

        // multi motor sequence test
        for (c=0; c<numMotors; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=4;
        }

        // CR Servo
        for (c=0; c<numCrServos; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=5;
        }

        // Run To Position Motors
        for (c=0; c<numRtpMotors; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=6;
        }

        // Servo Motors
        for (c=0; c<numServos; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=7;
        }

        // Axon Servo Motors
        for (c=0; c<numAxonServos; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=8;
        }


        // Mecanum
        for (c=0; c<numMecanum; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=9;
        }

        // Distance Sensor
        for (c=0; c<numDistanceSensor; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=10;
        }

        // KnyteVision
        for (c=0; c<numKnyteVision; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=11;
        }

        // Intake Color Sensor
        for (c=0; c<numIntakeSensors; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=12;
        }

        // LED lights
        for (c=0; c<numLED; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=13;
        }

        // Color Sensors
        for (c=0; c<numColorSensors; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=14;
        }

        // Alliance Color Sensors
        for (c=0; c<numAllianceColorSensors; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=15;
        }

        // GoBuilda Headlights
        for (c=0; c<numGoBuildaHeadlights; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=16;
        }

        // Chamber Distance Driver
        for (c=0; c<numChamberDistance; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=17;
        }

        // Limelight Driver
        for (c=0; c<numLimelight; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=18;
        }

        // Flywheel Driver
        for (c=0; c<numFlywheel; i++, c++)
        {
            IORegistry[i]=new IORegistryClass();
            IORegistry[i].driver=19;
        }


    }
    public void registerIODef(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap, Gamepad gp1, Gamepad gp2)
    {
        IORegistry[numRegistered].IOName=IOName;
        IORegistry[numRegistered].channel=channel;
        IORegistry[numRegistered].initVal=initVal;
        IORegistry[numRegistered].deviceName = deviceName;

        // TODO:  ADDING DRIVER: Add a new case (corresponds to the driver number assigned in constructor above) for your driver
        //        (see case 3 below for the SingleDCMotorDriver
        switch(IORegistry[numRegistered].driver) {
            case 0 : // InternalVariable Driver
                InternalVariableDriver.init(IOName, channel, initVal);
                break;

            case 1 : // GamePad Driver
                gamePadDriver.init(IOName, channel, initVal, gp1, gp2);
                break;

            case 2 : // Timer Driver
                TimerDriver.init(IOName, channel, initVal);
                break;

            case 3: // SingleDCMotor Driver
                singleDCMotorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 4: // Multi Driver
                multiMotorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 5: // CR Servo
                crServoDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 6: // RTP Motor driver
                rtpMotorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 7: // Servo
                servoDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 8: // Axon Servo Motors
                axonDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 9: // Servo Motors
                mecanumTeleopDriver.init(IOName, channel, initVal, deviceName, gp1, gp2, hwMap);
                break;

            case 10: // Distance Sensor
                distanceSensorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 11: // KnyteVision
                knyteVisionDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 12: // Intake Color Sensor
                intakeSensorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 13: // LED
                ledDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 14: // Color Sensor
                colorSensorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 15: // Alliance Color Sensor
                allianceSensorDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 16:
                goBuildaHeadlightDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 17:
                chamberDistanceDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 18:
                limelightDriver.init(IOName, channel, initVal, deviceName, hwMap);
                break;

            case 19:
                flywheelDriver.init(IOName, channel, initVal, deviceName, gp1, gp2, hwMap);
                break;

            default :
                break; // Do nothing
        }

        numRegistered++;
    }
    public void IORegistryBrokerSet(int IORegistryIndex, double setVal)
    {
        // TODO:  ADDING DRIVER: add another case for your new driver (see SingleDCMotorDriver for example)
        switch(IORegistry[IORegistryIndex].driver) {
            case 0 : // InternalVariable Driver
                InternalVariableDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break; // optional

            case 1 : // Gamepad driver
                gamePadDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 2 : // Timer Driver
                TimerDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break; // optional

            case 3: // SingleDCMotor Driver
                singleDCMotorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 4: // Multi Motor Driver
                multiMotorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 5: // CR Servo Driver
                crServoDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 6: // Run to Position Driver
                rtpMotorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 7: // Servo Driver
                servoDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 8: // Axon Servo Driver
                axonDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 9: // Mecanum Driver
                mecanumTeleopDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 10: // Distance Sensor
                distanceSensorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 11: // KnyteVision
                knyteVisionDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 12: // Intake Color Sensor
                intakeSensorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 13: // LED
                ledDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 14: // Color Sensors
                colorSensorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 15: // Alliance Color Sensors
                allianceSensorDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 16:
                goBuildaHeadlightDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 17:
                chamberDistanceDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 18:
                limelightDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            case 19:
                flywheelDriver.set(IORegistry[IORegistryIndex].channel, setVal);
                break;

            default :
                break; // Do nothing
        }
    }
    public double IORegistryBrokerGet(int IORegistryIndex)
    {
        double getVal=0.0;

        // TODO:  ADDING DRIVER: add another case for your new driver (see SingleDCMotorDriver for example)
        switch(IORegistry[IORegistryIndex].driver) {
            case 0 :
                getVal=InternalVariableDriver.get(IORegistry[IORegistryIndex].channel);
                break; // optional

            case 1 :
                getVal=gamePadDriver.get(IORegistry[IORegistryIndex].channel);
                break; // optional

            case 2 :
                getVal=TimerDriver.get(IORegistry[IORegistryIndex].channel);
                break; // optional

            case 3:
                getVal= singleDCMotorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 4:
                getVal= multiMotorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 5:
                getVal= crServoDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 6:
                getVal= rtpMotorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 7:
                getVal= servoDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 8:
                getVal= axonDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 9:
                getVal= mecanumTeleopDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 10:
                getVal= distanceSensorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 11:
                getVal= KnyteVisionDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 12:
                getVal= intakeSensorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 13:
                getVal= ledDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 14:
                getVal= colorSensorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 15:
                getVal= allianceSensorDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 16:
                getVal= goBuildaHeadlightDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 17:
                getVal= chamberDistanceDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 18:
                getVal= limelightDriver.get(IORegistry[IORegistryIndex].channel);
                break;

            case 19:
                getVal= flywheelDriver.get(IORegistry[IORegistryIndex].channel);
                break;


            default :
                break; // Do nothing
        }
        return(getVal);
    }
    public double IORegistryBrokerIndirection(char indirectionDriver, double indirectionValue)
    {
        double getVal=0.0;
        int indirectionIndex=(int) indirectionValue;

        switch(indirectionDriver) {
            case 'N' :
                getVal=InternalVariableDriver.get(indirectionIndex);
                break;

            case 'G' :
                getVal=gamePadDriver.get(indirectionIndex);
                break;

            case 'T' :
                getVal=TimerDriver.get(indirectionIndex);
                break;

            case '#' :
                getVal=indirectionValue;
                break;

            default :
                break; // Do nothing
        }
        return(getVal);
    }
}