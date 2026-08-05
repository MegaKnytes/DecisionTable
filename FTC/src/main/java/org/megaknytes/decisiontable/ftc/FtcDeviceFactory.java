package org.megaknytes.decisiontable.ftc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.config.DeviceFactory;
import org.megaknytes.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.ftc.drivers.OpModeDevice;
import org.megaknytes.decisiontable.ftc.drivers.HardwareMapDevice;

public class FtcDeviceFactory implements DeviceFactory {
    private final OpMode opMode;

    public FtcDeviceFactory(OpMode opMode) {
        this.opMode = opMode;
    }

    @Override
    public Device createAndBind(Class<?> driverClass, ParameterRegistry registry) throws ReflectiveOperationException {
        Device device = (Device) driverClass.getDeclaredConstructor().newInstance();

        if (device instanceof OpModeDevice) {
            ((OpModeDevice) device).registerConfiguration(opMode, registry);
        } else if (device instanceof HardwareMapDevice) {
            ((HardwareMapDevice) device).registerConfiguration(opMode.hardwareMap, registry);
        } else {
            throw new ConfigurationException("Driver class " + driverClass.getName() + " must implement OpModeDevice or HardwareMapDevice");
        }

        return device;
    }
}