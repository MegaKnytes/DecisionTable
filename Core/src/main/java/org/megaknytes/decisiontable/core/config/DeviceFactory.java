package org.megaknytes.decisiontable.core.config;

import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.registry.ParameterRegistry;

@FunctionalInterface
public interface DeviceFactory {
    Device createAndBind(Class<?> driverClass, ParameterRegistry registry) throws ReflectiveOperationException;
}