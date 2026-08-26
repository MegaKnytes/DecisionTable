package org.megaknytes.decisiontable.core.utils.device;

import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;

public interface Device {

    void registerParameters(ParameterRegistry registry);

    String getDeviceName();
}