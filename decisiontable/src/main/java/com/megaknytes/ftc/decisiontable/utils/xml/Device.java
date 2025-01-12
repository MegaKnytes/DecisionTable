package com.megaknytes.ftc.DecisionTable.utils.xml;

import com.megaknytes.ftc.DecisionTable.drivers.DTPDriver;

import java.util.Map;

public class Device {
    private final DTPDriver driver;
    private final Map<String, Object> config;

    public Device(DTPDriver driver, Map<String, Object> config) {
        this.driver = driver;
        this.config = config;
    }

    public DTPDriver getDriver() {
        return driver;
    }
    public Map<String, Object> getConfig() {
        return config;
    }
}