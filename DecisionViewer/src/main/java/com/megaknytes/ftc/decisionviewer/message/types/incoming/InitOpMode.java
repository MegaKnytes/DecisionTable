package com.megaknytes.ftc.decisionviewer.message.types.incoming;

import com.megaknytes.ftc.decisionviewer.message.Message;

public class InitOpMode extends Message {
    private final String opModeName;

    public InitOpMode(String opModeName) {
        this.opModeName = opModeName;
    }

    public String getOpModeName() {
        return opModeName;
    }
}
