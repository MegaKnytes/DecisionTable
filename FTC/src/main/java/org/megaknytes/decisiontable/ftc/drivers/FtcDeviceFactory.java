package org.megaknytes.decisiontable.ftc.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.utils.device.DeviceFactory;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;
import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;

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