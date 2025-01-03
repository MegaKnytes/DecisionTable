package com.MegaKnytes.DecisionTable.drivers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.List;
import java.util.Map;

public interface DTPDriver {
    void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions);
    void set(String param, Object value);
    Object get(String value);
}