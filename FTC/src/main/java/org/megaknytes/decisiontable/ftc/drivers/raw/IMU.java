package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class IMU implements HardwareMapDevice {
    private com.qualcomm.robotcore.hardware.IMU imu;
    private RevHubOrientationOnRobot.LogoFacingDirection logoFacing;
    private RevHubOrientationOnRobot.UsbFacingDirection usbFacing;

    @Override
    public void registerConfiguration(HardwareMap hardwareMap, ParameterRegistry registry) {
        registry.createParameter(this, "HardwareMap", String.class,
                        () -> imu.getDeviceName(),
                        (name) -> imu = hardwareMap.get(com.qualcomm.robotcore.hardware.IMU.class, name))
                .addSubParameter("LogoFacing", RevHubOrientationOnRobot.LogoFacingDirection.class,
                        () -> logoFacing,
                        (direction) -> {
                            logoFacing = direction;
                            initializeIfReady();
                        })
                .addSubParameter("UsbFacing", RevHubOrientationOnRobot.UsbFacingDirection.class,
                        () -> usbFacing,
                        (direction) -> {
                            usbFacing = direction;
                            initializeIfReady();
                        });
    }

    private void initializeIfReady() {
        if (logoFacing != null && usbFacing != null) {
            imu.initialize(new com.qualcomm.robotcore.hardware.IMU.Parameters(new RevHubOrientationOnRobot(logoFacing, usbFacing)));
        }
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Yaw", Double.class, () -> imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
        registry.createParameter(this, "Pitch", Double.class, () -> imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES));
        registry.createParameter(this, "Roll", Double.class, () -> imu.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES));
        registry.createParameter(this, "ResetYaw", Boolean.class, () -> false, (trigger) -> imu.resetYaw());
    }

    @Override
    public String getDeviceName() {
        return "IMU";
    }
}