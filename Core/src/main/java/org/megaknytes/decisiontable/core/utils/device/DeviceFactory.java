package org.megaknytes.decisiontable.core.utils.device;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;

@FunctionalInterface
public interface DeviceFactory {
    Device createAndBind(Class<?> driverClass, ParameterRegistry registry) throws ReflectiveOperationException;
}