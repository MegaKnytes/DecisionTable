package org.megaknytes.ftc.decisiontable.core.drivers.common;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.Enabled;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@Enabled
public class CRServoDriver implements DTDevice {
    private static final Logger logger = Logger.getLogger(CRServoDriver.class.getName());
    private CRServo crServo;

    @Override
    public void setup(OpMode opMode, String deviceName) {
        try {
            crServo = opMode.hardwareMap.crservo.get(deviceName);
            logger.log(Level.INFO, "CRServo initialized: " + deviceName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize CRServo: " + deviceName, e);
            throw new RuntimeException("Failed to initialize CRServo: " + deviceName, e);
        }
    }

    @Override
    public void registerParameters(String deviceName, ParameterRegistry registry) {
        Supplier<DcMotorSimple.Direction> directionSupplier = () -> crServo.getDirection();
        Supplier<Double> powerSupplier = () -> crServo.getPower();

        Parameter<DcMotorSimple.Direction> direction = registry.createParameter(deviceName, "direction", DcMotorSimple.Direction.class, directionSupplier);
        Parameter<Double> power = registry.createParameter(deviceName, "power", Double.class, powerSupplier);

        power.addListener(newPower -> {
            crServo.setPower(newPower);
        });

        direction.addListener(newDirection -> {
            crServo.setDirection(newDirection);
        });
    }
}