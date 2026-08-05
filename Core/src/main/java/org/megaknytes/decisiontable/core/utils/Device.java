package org.megaknytes.decisiontable.core.utils;

import org.megaknytes.decisiontable.core.registry.ParameterRegistry;

public interface Device {

    void registerParameters(ParameterRegistry registry);

    String getDeviceName();
}