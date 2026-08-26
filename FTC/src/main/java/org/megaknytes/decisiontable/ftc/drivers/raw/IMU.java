package org.megaknytes.decisiontable.ftc.drivers.raw;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.utils.device.InitializedDevice;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class IMU implements HardwareMapDevice, InitializedDevice {
    private com.qualcomm.robotcore.hardware.IMU imu;
    private AngleUnit angleUnit = AngleUnit.DEGREES;
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
                        })
                .addSubParameter("UsbFacing", RevHubOrientationOnRobot.UsbFacingDirection.class,
                        () -> usbFacing,
                        (direction) -> {
                            usbFacing = direction;
                        })
                .addSubParameter("AngleUnit", AngleUnit.class,
                        () -> angleUnit,
                        (unit) -> angleUnit = unit);
    }

    @Override
    public void initialize() {
        if (logoFacing != null && usbFacing != null) {
            imu.initialize(new com.qualcomm.robotcore.hardware.IMU.Parameters(new RevHubOrientationOnRobot(logoFacing, usbFacing)));
        } else {
            throw new IllegalStateException("IMU orientation parameters not set. Please configure LogoFacing and UsbFacing before initialization.");
        }
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.createParameter(this, "Yaw", Double.class, () -> imu.getRobotYawPitchRollAngles().getYaw(angleUnit));
        registry.createParameter(this, "Pitch", Double.class, () -> imu.getRobotYawPitchRollAngles().getPitch(angleUnit));
        registry.createParameter(this, "Roll", Double.class, () -> imu.getRobotYawPitchRollAngles().getRoll(angleUnit));
        registry.createParameter(this, "ResetYaw", Boolean.class, () -> false, (trigger) -> imu.resetYaw());
    }

    @Override
    public String getDeviceName() {
        return "IMU";
    }
}