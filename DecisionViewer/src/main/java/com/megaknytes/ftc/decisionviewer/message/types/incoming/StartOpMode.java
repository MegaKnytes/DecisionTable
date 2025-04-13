package com.megaknytes.ftc.decisionviewer.message.types.incoming;

import com.megaknytes.ftc.decisionviewer.message.Message;

public class StartOpMode extends Message {
    private final String opModeName;

    public StartOpMode(String opModeName) {
        this.opModeName = opModeName;
    }

    public String getOpModeName() {
        return opModeName;
    }
}
