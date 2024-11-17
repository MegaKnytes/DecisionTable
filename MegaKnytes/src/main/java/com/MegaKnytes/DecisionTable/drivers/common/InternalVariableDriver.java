package com.MegaKnytes.DecisionTable.drivers.common;

import com.MegaKnytes.DecisionTable.drivers.DTPDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.List;
import java.util.Map;

public class InternalVariableDriver implements DTPDriver {
    private Map<String, Object> internal;

    @Override
    public void setup(OpMode opMode, String deviceName, Map<String, Object> deviceOptions) {
        // Nothing to see here...
    }

    @Override
    public void set(String param, Object value) {
        internal.put(param, value);
    }

    @Override
    public Object get(String value) {
        return internal.get(value);
    }
}
